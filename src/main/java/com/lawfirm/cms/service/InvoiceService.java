package com.lawfirm.cms.service;

import com.lawfirm.cms.model.Invoice;
import com.lawfirm.cms.model.LegalCase;
import com.lawfirm.cms.model.User;
import com.lawfirm.cms.repository.InvoiceRepository;
import com.lawfirm.cms.strategy.FlatFeeStrategy;
import com.lawfirm.cms.strategy.HourlyRateStrategy;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final HourlyRateStrategy hourlyRateStrategy;
    private final FlatFeeStrategy flatFeeStrategy;
    private final NotificationService notificationService;

    public InvoiceService(InvoiceRepository invoiceRepository,
                          HourlyRateStrategy hourlyRateStrategy,
                          FlatFeeStrategy flatFeeStrategy,
                          NotificationService notificationService) {
        this.invoiceRepository = invoiceRepository;
        this.hourlyRateStrategy = hourlyRateStrategy;
        this.flatFeeStrategy = flatFeeStrategy;
        this.notificationService = notificationService;
    }

    public List<Invoice> findByCase(LegalCase legalCase) {
        return invoiceRepository.findByLegalCase(legalCase);
    }

    public Optional<Invoice> findById(Long id) {
        return invoiceRepository.findById(id);
    }

    public List<Invoice> findUnpaid() {
        return invoiceRepository.findByPaid(false);
    }

    public List<Invoice> findAll() {
        return invoiceRepository.findAll();
    }

    public List<Invoice> findByClient(User client) {
        return invoiceRepository.findByClient(client);
    }

    public List<Invoice> findByLawyer(User lawyer) {
        return invoiceRepository.findByLawyer(lawyer);
    }

    public Invoice generateInvoice(LegalCase legalCase, double hours, double rate, String billingType) {
        Invoice invoice = new Invoice();
        invoice.setLegalCase(legalCase);
        invoice.setHours(hours);
        invoice.setRate(rate);
        invoice.setBillingType(billingType);

        double amount;
        if ("HOURLY".equalsIgnoreCase(billingType)) {
            amount = hourlyRateStrategy.calculateFee(hours, rate);
        } else {
            amount = flatFeeStrategy.calculateFee(hours, rate);
        }
        invoice.setAmount(amount);

        String invoiceNumber = "INV-" + System.currentTimeMillis();
        invoice.setInvoiceNumber(invoiceNumber);
        invoice.setGeneratedDate(LocalDate.now());
        invoice.setDueDate(LocalDate.now().plusDays(30));
        invoice.setPaid(false);

        Invoice saved = invoiceRepository.save(invoice);
        notificationService.notifyObservers("Invoice generated: " + invoiceNumber, legalCase.getId());
        return saved;
    }

    public Invoice markPaid(Long invoiceId) {
        Optional<Invoice> opt = invoiceRepository.findById(invoiceId);
        if (opt.isPresent()) {
            Invoice invoice = opt.get();
            invoice.setPaid(true);
            Invoice saved = invoiceRepository.save(invoice);
            notificationService.notifyObservers("Invoice paid: " + invoice.getInvoiceNumber(),
                    invoice.getLegalCase().getId());
            return saved;
        }
        throw new RuntimeException("Invoice not found with id: " + invoiceId);
    }

    public void deleteInvoice(Long id) {
        invoiceRepository.deleteById(id);
    }
}
