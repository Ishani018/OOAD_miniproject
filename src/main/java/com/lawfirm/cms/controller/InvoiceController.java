package com.lawfirm.cms.controller;

import com.lawfirm.cms.model.Invoice;
import com.lawfirm.cms.model.LegalCase;
import com.lawfirm.cms.model.User;
import com.lawfirm.cms.service.CaseService;
import com.lawfirm.cms.service.InvoiceService;
import com.lawfirm.cms.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
public class InvoiceController {

    private final InvoiceService invoiceService;
    private final CaseService caseService;
    private final UserService userService;

    public InvoiceController(InvoiceService invoiceService, CaseService caseService, UserService userService) {
        this.invoiceService = invoiceService;
        this.caseService = caseService;
        this.userService = userService;
    }

    @GetMapping("/invoices")
    public String listInvoices(Model model, Principal principal) {
        Optional<User> optUser = userService.findByEmail(principal.getName());
        if (optUser.isEmpty()) {
            return "redirect:/login";
        }
        User user = optUser.get();
        List<Invoice> invoices;
        
        if ("CLIENT".equals(user.getRole())) {
            invoices = invoiceService.findByClient(user);
        } else if ("LAWYER".equals(user.getRole())) {
            invoices = invoiceService.findByLawyer(user);
        } else {
            invoices = invoiceService.findAll();
        }
        
        model.addAttribute("invoices", invoices);
        model.addAttribute("userName", user.getName());
        return "invoices/list";
    }

    @GetMapping("/cases/{caseId}/invoices/create")
    public String createInvoiceForm(@PathVariable Long caseId, Model model) {
        Optional<LegalCase> optCase = caseService.findById(caseId);
        if (optCase.isEmpty()) {
            return "redirect:/cases";
        }
        model.addAttribute("legalCase", optCase.get());
        model.addAttribute("caseId", caseId);
        return "invoices/create";
    }

    @PostMapping("/cases/{caseId}/invoices/create")
    public String createInvoice(@PathVariable Long caseId,
                                @RequestParam double hours,
                                @RequestParam double rate,
                                @RequestParam String billingType,
                                RedirectAttributes redirectAttributes) {
        try {
            Optional<LegalCase> optCase = caseService.findById(caseId);
            if (optCase.isPresent()) {
                invoiceService.generateInvoice(optCase.get(), hours, rate, billingType);
                redirectAttributes.addFlashAttribute("success", "Invoice generated successfully!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Case not found.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to generate invoice: " + e.getMessage());
        }
        return "redirect:/cases/" + caseId;
    }

    @GetMapping("/invoices/{id}")
    public String viewInvoice(@PathVariable Long id, Model model) {
        Optional<Invoice> optInvoice = invoiceService.findById(id);
        if (optInvoice.isEmpty()) {
            return "redirect:/cases";
        }
        model.addAttribute("invoice", optInvoice.get());
        return "invoices/view";
    }

    @PostMapping("/invoices/{id}/pay")
    public String markPaid(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            Invoice invoice = invoiceService.markPaid(id);
            redirectAttributes.addFlashAttribute("success", "Invoice marked as paid!");
            return "redirect:/cases/" + invoice.getLegalCase().getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to mark invoice as paid: " + e.getMessage());
            return "redirect:/cases";
        }
    }
}
