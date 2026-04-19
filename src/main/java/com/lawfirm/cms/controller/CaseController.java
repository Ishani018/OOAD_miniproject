package com.lawfirm.cms.controller;

import com.lawfirm.cms.model.CaseStatus;
import com.lawfirm.cms.model.LegalCase;
import com.lawfirm.cms.model.User;
import com.lawfirm.cms.service.CaseService;
import com.lawfirm.cms.service.DocumentService;
import com.lawfirm.cms.service.HearingService;
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
public class CaseController {

    private final CaseService caseService;
    private final UserService userService;
    private final DocumentService documentService;
    private final HearingService hearingService;
    private final InvoiceService invoiceService;

    public CaseController(CaseService caseService, UserService userService,
                          DocumentService documentService, HearingService hearingService,
                          InvoiceService invoiceService) {
        this.caseService = caseService;
        this.userService = userService;
        this.documentService = documentService;
        this.hearingService = hearingService;
        this.invoiceService = invoiceService;
    }

    @GetMapping("/cases")
    public String listCases(Model model, Principal principal) {
        Optional<User> optUser = userService.findByEmail(principal.getName());
        if (optUser.isEmpty()) {
            return "redirect:/login";
        }
        User user = optUser.get();
        List<LegalCase> cases;

        switch (user.getRole()) {
            case "LAWYER":
                cases = caseService.findByLawyer(user);
                break;
            case "CLIENT":
                cases = caseService.findByClient(user);
                break;
            default:
                cases = caseService.findAll();
                break;
        }

        model.addAttribute("cases", cases);
        model.addAttribute("user", user);
        return "cases/list";
    }

    @GetMapping("/cases/create")
    public String createCaseForm(Model model) {
        model.addAttribute("lawyers", userService.findByRole("LAWYER"));
        model.addAttribute("clients", userService.findByRole("CLIENT"));
        return "cases/create";
    }

    @PostMapping("/cases/create")
    public String createCase(@RequestParam String type,
                             @RequestParam String title,
                             @RequestParam String description,
                             @RequestParam Long lawyerId,
                             @RequestParam Long clientId,
                             RedirectAttributes redirectAttributes) {
        try {
            Optional<User> lawyer = userService.findById(lawyerId);
            Optional<User> client = userService.findById(clientId);
            if (lawyer.isPresent() && client.isPresent()) {
                caseService.createCase(type, title, description, lawyer.get(), client.get());
                redirectAttributes.addFlashAttribute("success", "Case created successfully!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Lawyer or Client not found.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to create case: " + e.getMessage());
        }
        return "redirect:/cases";
    }

    @GetMapping("/cases/{id}")
    public String caseDetail(@PathVariable Long id, Model model) {
        Optional<LegalCase> optCase = caseService.findById(id);
        if (optCase.isEmpty()) {
            return "redirect:/cases";
        }
        LegalCase legalCase = optCase.get();
        model.addAttribute("legalCase", legalCase);
        model.addAttribute("documents", documentService.findByCase(legalCase));
        model.addAttribute("hearings", hearingService.findByCase(legalCase));
        model.addAttribute("invoices", invoiceService.findByCase(legalCase));
        model.addAttribute("lawyers", userService.findByRole("LAWYER"));
        model.addAttribute("statuses", CaseStatus.values());
        return "cases/view";
    }

    @GetMapping("/cases/{id}/edit")
    public String editCaseForm(@PathVariable Long id, Model model) {
        Optional<LegalCase> optCase = caseService.findById(id);
        if (optCase.isEmpty()) {
            return "redirect:/cases";
        }
        model.addAttribute("legalCase", optCase.get());
        model.addAttribute("lawyers", userService.findByRole("LAWYER"));
        model.addAttribute("clients", userService.findByRole("CLIENT"));
        model.addAttribute("statuses", CaseStatus.values());
        return "cases/edit";
    }

    @PostMapping("/cases/{id}/edit")
    public String editCase(@PathVariable Long id,
                           @RequestParam String title,
                           @RequestParam String description,
                           RedirectAttributes redirectAttributes) {
        try {
            Optional<LegalCase> optCase = caseService.findById(id);
            if (optCase.isPresent()) {
                LegalCase legalCase = optCase.get();
                legalCase.setTitle(title);
                legalCase.setDescription(description);
                caseService.saveCase(legalCase);
                redirectAttributes.addFlashAttribute("success", "Case updated successfully!");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update case: " + e.getMessage());
        }
        return "redirect:/cases/" + id;
    }

    @PostMapping("/cases/{id}/status")
    public String updateStatus(@PathVariable Long id,
                               @RequestParam String status,
                               RedirectAttributes redirectAttributes) {
        try {
            caseService.updateStatus(id, CaseStatus.valueOf(status));
            redirectAttributes.addFlashAttribute("success", "Case status updated!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to update status: " + e.getMessage());
        }
        return "redirect:/cases/" + id;
    }

    @PostMapping("/cases/{id}/assign")
    public String assignLawyer(@PathVariable Long id,
                               @RequestParam Long lawyerId,
                               RedirectAttributes redirectAttributes) {
        try {
            Optional<User> lawyer = userService.findById(lawyerId);
            if (lawyer.isPresent()) {
                caseService.assignLawyer(id, lawyer.get());
                redirectAttributes.addFlashAttribute("success", "Lawyer assigned successfully!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Lawyer not found.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to assign lawyer: " + e.getMessage());
        }
        return "redirect:/cases/" + id;
    }

    @PostMapping("/cases/{id}/activate")
    public String activateCase(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            caseService.activateCase(id);
            redirectAttributes.addFlashAttribute("success", "Case activated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to activate case: " + e.getMessage());
        }
        return "redirect:/cases/" + id;
    }

    @PostMapping("/cases/{id}/reject")
    public String rejectCase(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            caseService.rejectCase(id);
            redirectAttributes.addFlashAttribute("success", "Case rejected.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to reject case: " + e.getMessage());
        }
        return "redirect:/cases/" + id;
    }

    @PostMapping("/cases/{id}/close")
    public String closeCase(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            caseService.closeCase(id);
            redirectAttributes.addFlashAttribute("success", "Case closed successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to close case: " + e.getMessage());
        }
        return "redirect:/cases/" + id;
    }

    @PostMapping("/cases/{id}/reopen")
    public String reopenCase(@PathVariable Long id,
                             @RequestParam(required = false) Long lawyerId,
                             RedirectAttributes redirectAttributes) {
        try {
            User newLawyer = null;
            if (lawyerId != null) {
                Optional<User> optLawyer = userService.findById(lawyerId);
                if (optLawyer.isPresent()) {
                    newLawyer = optLawyer.get();
                }
            }
            caseService.reopenCase(id, newLawyer);
            redirectAttributes.addFlashAttribute("success", "Case reopened successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to reopen case: " + e.getMessage());
        }
        return "redirect:/cases/" + id;
    }

    @PostMapping("/cases/{id}/delete")
    public String deleteCase(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            caseService.deleteCase(id);
            redirectAttributes.addFlashAttribute("success", "Case deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete case: " + e.getMessage());
        }
        return "redirect:/cases";
    }
}
