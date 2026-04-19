package com.lawfirm.cms.service;

import com.lawfirm.cms.factory.CaseFactory;
import com.lawfirm.cms.model.CaseStatus;
import com.lawfirm.cms.model.LegalCase;
import com.lawfirm.cms.model.User;
import com.lawfirm.cms.repository.CaseRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class CaseService {

    private final CaseRepository caseRepository;
    private final CaseFactory caseFactory;
    private final NotificationService notificationService;

    public CaseService(CaseRepository caseRepository, CaseFactory caseFactory,
                       NotificationService notificationService) {
        this.caseRepository = caseRepository;
        this.caseFactory = caseFactory;
        this.notificationService = notificationService;
    }

    public List<LegalCase> findAll() {
        return caseRepository.findAll();
    }

    public Optional<LegalCase> findById(Long id) {
        return caseRepository.findById(id);
    }

    public List<LegalCase> findByLawyer(User lawyer) {
        return caseRepository.findByAssignedLawyer(lawyer);
    }

    public List<LegalCase> findByClient(User client) {
        return caseRepository.findByClient(client);
    }

    public List<LegalCase> findByStatus(CaseStatus status) {
        return caseRepository.findByStatus(status);
    }

    public LegalCase createCase(String type, String title, String description,
                                 User lawyer, User client) {
        LegalCase legalCase = caseFactory.createCase(type, title, description);
        legalCase.setAssignedLawyer(lawyer);
        legalCase.setClient(client);
        legalCase.setStatus(CaseStatus.UNDER_REVIEW);
        LegalCase saved = caseRepository.save(legalCase);
        notificationService.notifyObservers("New case created: " + title, saved.getId());
        return saved;
    }

    public LegalCase updateStatus(Long caseId, CaseStatus status) {
        Optional<LegalCase> opt = caseRepository.findById(caseId);
        if (opt.isPresent()) {
            LegalCase legalCase = opt.get();
            legalCase.setStatus(status);
            LegalCase saved = caseRepository.save(legalCase);
            notificationService.notifyObservers("Case status updated to " + status + ": " + legalCase.getTitle(), saved.getId());
            return saved;
        }
        throw new RuntimeException("Case not found with id: " + caseId);
    }

    public LegalCase assignLawyer(Long caseId, User lawyer) {
        Optional<LegalCase> opt = caseRepository.findById(caseId);
        if (opt.isPresent()) {
            LegalCase legalCase = opt.get();
            legalCase.setAssignedLawyer(lawyer);
            LegalCase saved = caseRepository.save(legalCase);
            notificationService.notifyObservers("Lawyer assigned to case: " + legalCase.getTitle(), saved.getId());
            return saved;
        }
        throw new RuntimeException("Case not found with id: " + caseId);
    }

    public LegalCase closeCase(Long caseId) {
        Optional<LegalCase> opt = caseRepository.findById(caseId);
        if (opt.isPresent()) {
            LegalCase legalCase = opt.get();
            legalCase.setStatus(CaseStatus.CLOSED);
            legalCase.setClosingDate(LocalDate.now());
            LegalCase saved = caseRepository.save(legalCase);
            notificationService.notifyObservers("Case closed: " + legalCase.getTitle(), saved.getId());
            return saved;
        }
        throw new RuntimeException("Case not found with id: " + caseId);
    }

    public LegalCase reopenCase(Long caseId) {
        return reopenCase(caseId, null);
    }

    public LegalCase reopenCase(Long caseId, User newLawyer) {
        Optional<LegalCase> opt = caseRepository.findById(caseId);
        if (opt.isPresent()) {
            LegalCase legalCase = opt.get();

            // Validate: only CLOSED or ARCHIVED cases can be reopened
            if (legalCase.getStatus() != CaseStatus.CLOSED && legalCase.getStatus() != CaseStatus.ARCHIVED) {
                throw new RuntimeException("Only closed or archived cases can be reopened. Current status: " + legalCase.getStatus());
            }

            // Step 1: Transition to REOPENED (lawyer reassignment happens here)
            legalCase.setStatus(CaseStatus.REOPENED);
            if (newLawyer != null) {
                legalCase.setAssignedLawyer(newLawyer);
            }
            caseRepository.save(legalCase);
            notificationService.notifyObservers("Case reopened (reassigning lawyer): " + legalCase.getTitle(), legalCase.getId());

            // Step 2: Transition to ACTIVE (case is now fully reactivated)
            legalCase.setStatus(CaseStatus.ACTIVE);
            legalCase.setClosingDate(null);
            LegalCase saved = caseRepository.save(legalCase);
            notificationService.notifyObservers("Case reactivated: " + legalCase.getTitle(), saved.getId());
            return saved;
        }
        throw new RuntimeException("Case not found with id: " + caseId);
    }

    public LegalCase activateCase(Long caseId) {
        Optional<LegalCase> opt = caseRepository.findById(caseId);
        if (opt.isPresent()) {
            LegalCase legalCase = opt.get();
            if (legalCase.getStatus() != CaseStatus.UNDER_REVIEW) {
                throw new RuntimeException("Only cases under review can be activated. Current status: " + legalCase.getStatus());
            }
            legalCase.setStatus(CaseStatus.ACTIVE);
            LegalCase saved = caseRepository.save(legalCase);
            notificationService.notifyObservers("Case activated: " + legalCase.getTitle(), saved.getId());
            return saved;
        }
        throw new RuntimeException("Case not found with id: " + caseId);
    }

    public LegalCase rejectCase(Long caseId) {
        Optional<LegalCase> opt = caseRepository.findById(caseId);
        if (opt.isPresent()) {
            LegalCase legalCase = opt.get();
            if (legalCase.getStatus() != CaseStatus.UNDER_REVIEW) {
                throw new RuntimeException("Only cases under review can be rejected. Current status: " + legalCase.getStatus());
            }
            legalCase.setStatus(CaseStatus.REJECTED);
            LegalCase saved = caseRepository.save(legalCase);
            notificationService.notifyObservers("Case rejected: " + legalCase.getTitle(), saved.getId());
            return saved;
        }
        throw new RuntimeException("Case not found with id: " + caseId);
    }

    public LegalCase saveCase(LegalCase legalCase) {
        return caseRepository.save(legalCase);
    }

    public void deleteCase(Long id) {
        caseRepository.deleteById(id);
    }
}
