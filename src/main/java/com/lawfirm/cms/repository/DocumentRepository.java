package com.lawfirm.cms.repository;

import com.lawfirm.cms.model.Document;
import com.lawfirm.cms.model.LegalCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByLegalCase(LegalCase legalCase);
}
