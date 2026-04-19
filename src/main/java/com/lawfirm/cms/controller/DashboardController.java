package com.lawfirm.cms.controller;

import com.lawfirm.cms.model.CaseStatus;
import com.lawfirm.cms.model.Hearing;
import com.lawfirm.cms.model.Invoice;
import com.lawfirm.cms.model.LegalCase;
import com.lawfirm.cms.model.User;
import com.lawfirm.cms.service.CaseService;
import com.lawfirm.cms.service.HearingService;
import com.lawfirm.cms.service.InvoiceService;
import com.lawfirm.cms.service.NotificationQueryService;
import com.lawfirm.cms.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class DashboardController {

    private final UserService userService;
    private final CaseService caseService;
    private final HearingService hearingService;
    private final InvoiceService invoiceService;
    private final NotificationQueryService notificationQueryService;

    public DashboardController(UserService userService, CaseService caseService,
                               HearingService hearingService, InvoiceService invoiceService,
                               NotificationQueryService notificationQueryService) {
        this.userService = userService;
        this.caseService = caseService;
        this.hearingService = hearingService;
        this.invoiceService = invoiceService;
        this.notificationQueryService = notificationQueryService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal) {
        Optional<User> optUser = userService.findByEmail(principal.getName());
        if (optUser.isEmpty()) {
            return "redirect:/login";
        }
        User user = optUser.get();
        String role = user.getRole();

        model.addAttribute("user", user);
        model.addAttribute("userName", user.getName());
        model.addAttribute("role", role);

        int unreadCount = notificationQueryService.getUnreadCount(user.getId());
        model.addAttribute("unreadNotifications", unreadCount);
        model.addAttribute("notificationCount", unreadCount);

        switch (role) {
            case "ADMIN":
                List<LegalCase> allCases = caseService.findAll();
                List<User> allUsers = userService.findAll();
                long activeCasesAdmin = allCases.stream()
                        .filter(c -> c.getStatus() == CaseStatus.ACTIVE).count();
                long pendingInvoicesAdmin = invoiceService.findUnpaid().size();

                model.addAttribute("cases", allCases);
                model.addAttribute("recentCases", allCases);
                model.addAttribute("users", allUsers);
                model.addAttribute("totalCases", allCases.size());
                model.addAttribute("activeCases", activeCasesAdmin);
                model.addAttribute("totalUsers", allUsers.size());
                model.addAttribute("pendingInvoices", pendingInvoicesAdmin);
                break;

            case "LAWYER":
                List<LegalCase> lawyerCases = caseService.findByLawyer(user);
                long activeL = lawyerCases.stream()
                        .filter(c -> c.getStatus() == CaseStatus.ACTIVE).count();
                List<Hearing> upcomingH = hearingService.findUpcoming();
                long pendingInvL = 0;
                for (LegalCase lc : lawyerCases) {
                    pendingInvL += invoiceService.findByCase(lc).stream()
                            .filter(inv -> !inv.isPaid()).count();
                }

                model.addAttribute("cases", lawyerCases);
                model.addAttribute("assignedCases", lawyerCases);
                model.addAttribute("myCases", lawyerCases.size());
                model.addAttribute("activeCases", activeL);
                model.addAttribute("upcomingHearings", upcomingH.size());
                model.addAttribute("hearingsList", upcomingH);
                model.addAttribute("pendingInvoices", pendingInvL);
                break;

            case "STAFF":
                List<LegalCase> staffCases = caseService.findAll();
                List<Hearing> allHearings = hearingService.findUpcoming();

                model.addAttribute("cases", staffCases);
                model.addAttribute("recentCases", staffCases);
                model.addAttribute("totalCases", staffCases.size());
                model.addAttribute("upcomingHearings", allHearings.size());
                model.addAttribute("hearingsList", allHearings);
                model.addAttribute("allHearings", allHearings);
                break;

            case "CLIENT":
                List<LegalCase> clientCases = caseService.findByClient(user);
                List<Invoice> unpaidInvoices = new ArrayList<>();
                for (LegalCase c : clientCases) {
                    List<Invoice> caseInvoices = invoiceService.findByCase(c);
                    for (Invoice inv : caseInvoices) {
                        if (!inv.isPaid()) {
                            unpaidInvoices.add(inv);
                        }
                    }
                }

                model.addAttribute("cases", clientCases);
                model.addAttribute("clientCases", clientCases);
                model.addAttribute("myCases", clientCases.size());
                model.addAttribute("pendingBills", unpaidInvoices.size());
                model.addAttribute("unpaidInvoices", unpaidInvoices);
                break;

            default:
                break;
        }

        return "dashboard";
    }
}
