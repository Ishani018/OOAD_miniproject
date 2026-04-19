package com.lawfirm.cms.repository;

import com.lawfirm.cms.model.CaseStatus;
import com.lawfirm.cms.model.LegalCase;
import com.lawfirm.cms.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CaseRepository extends JpaRepository<LegalCase, Long> {
    List<LegalCase> findByAssignedLawyer(User lawyer);
    List<LegalCase> findByClient(User client);
    List<LegalCase> findByStatus(CaseStatus status);
    Optional<LegalCase> findByCaseNumber(String caseNumber);
}
