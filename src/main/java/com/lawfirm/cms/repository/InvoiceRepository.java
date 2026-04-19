package com.lawfirm.cms.repository;

import com.lawfirm.cms.model.Invoice;
import com.lawfirm.cms.model.LegalCase;
import com.lawfirm.cms.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {
    List<Invoice> findByLegalCase(LegalCase legalCase);
    List<Invoice> findByPaid(boolean paid);
    
    @Query("SELECT i FROM Invoice i WHERE i.legalCase.client = :client")
    List<Invoice> findByClient(@Param("client") User client);
    
    @Query("SELECT i FROM Invoice i WHERE i.legalCase.assignedLawyer = :lawyer")
    List<Invoice> findByLawyer(@Param("lawyer") User lawyer);
}
