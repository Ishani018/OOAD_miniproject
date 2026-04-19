package com.lawfirm.cms.repository;

import com.lawfirm.cms.model.Hearing;
import com.lawfirm.cms.model.LegalCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HearingRepository extends JpaRepository<Hearing, Long> {
    List<Hearing> findByLegalCase(LegalCase legalCase);
    List<Hearing> findByDateTimeBetween(LocalDateTime start, LocalDateTime end);
}
