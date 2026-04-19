package com.lawfirm.cms.service;

import com.lawfirm.cms.model.Hearing;
import com.lawfirm.cms.model.LegalCase;
import com.lawfirm.cms.repository.HearingRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class HearingService {

    private final HearingRepository hearingRepository;
    private final NotificationService notificationService;

    public HearingService(HearingRepository hearingRepository,
                          NotificationService notificationService) {
        this.hearingRepository = hearingRepository;
        this.notificationService = notificationService;
    }

    public List<Hearing> findByCase(LegalCase legalCase) {
        return hearingRepository.findByLegalCase(legalCase);
    }

    public Optional<Hearing> findById(Long id) {
        return hearingRepository.findById(id);
    }

    public Hearing scheduleHearing(LegalCase legalCase, LocalDateTime dateTime,
                                    String location, String judgeName) {
        Hearing hearing = new Hearing();
        hearing.setLegalCase(legalCase);
        hearing.setDateTime(dateTime);
        hearing.setLocation(location);
        hearing.setJudgeName(judgeName);
        hearing.setStatus("SCHEDULED");
        Hearing saved = hearingRepository.save(hearing);
        notificationService.notifyObservers("Hearing scheduled for case: " + legalCase.getTitle(), legalCase.getId());
        return saved;
    }

    public Hearing reschedule(Long hearingId, LocalDateTime newDate) {
        Optional<Hearing> opt = hearingRepository.findById(hearingId);
        if (opt.isPresent()) {
            Hearing hearing = opt.get();
            hearing.setDateTime(newDate);
            hearing.setStatus("SCHEDULED");
            Hearing saved = hearingRepository.save(hearing);
            notificationService.notifyObservers("Hearing rescheduled for case: " + hearing.getLegalCase().getTitle(),
                    hearing.getLegalCase().getId());
            return saved;
        }
        throw new RuntimeException("Hearing not found with id: " + hearingId);
    }

    public Hearing adjournHearing(Long hearingId, String reason) {
        Optional<Hearing> opt = hearingRepository.findById(hearingId);
        if (opt.isPresent()) {
            Hearing hearing = opt.get();
            hearing.setStatus("ADJOURNED");
            hearing.setComments(reason);
            Hearing saved = hearingRepository.save(hearing);
            notificationService.notifyObservers("Hearing adjourned for case: " + hearing.getLegalCase().getTitle(),
                    hearing.getLegalCase().getId());
            return saved;
        }
        throw new RuntimeException("Hearing not found with id: " + hearingId);
    }

    public Hearing completeHearing(Long hearingId, String outcome) {
        Optional<Hearing> opt = hearingRepository.findById(hearingId);
        if (opt.isPresent()) {
            Hearing hearing = opt.get();
            hearing.setStatus("COMPLETED");
            hearing.setComments(outcome);
            Hearing saved = hearingRepository.save(hearing);
            notificationService.notifyObservers("Hearing completed for case: " + hearing.getLegalCase().getTitle(),
                    hearing.getLegalCase().getId());
            return saved;
        }
        throw new RuntimeException("Hearing not found with id: " + hearingId);
    }

    public void cancelHearing(Long id) {
        Optional<Hearing> opt = hearingRepository.findById(id);
        if (opt.isPresent()) {
            Hearing hearing = opt.get();
            hearing.setStatus("CANCELLED");
            hearingRepository.save(hearing);
        }
    }

    public List<Hearing> findUpcoming() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime thirtyDaysLater = now.plusDays(30);
        return hearingRepository.findByDateTimeBetween(now, thirtyDaysLater);
    }

    public List<Hearing> findAll() {
        return hearingRepository.findAll();
    }
}
