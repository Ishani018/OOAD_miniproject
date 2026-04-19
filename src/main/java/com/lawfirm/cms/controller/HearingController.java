package com.lawfirm.cms.controller;

import com.lawfirm.cms.model.Hearing;
import com.lawfirm.cms.model.LegalCase;
import com.lawfirm.cms.service.CaseService;
import com.lawfirm.cms.service.HearingService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Controller
public class HearingController {

    private final HearingService hearingService;
    private final CaseService caseService;

    public HearingController(HearingService hearingService, CaseService caseService) {
        this.hearingService = hearingService;
        this.caseService = caseService;
    }

    @GetMapping("/hearings")
    public String listHearings(Model model) {
        List<Hearing> hearings = hearingService.findAll();
        model.addAttribute("hearings", hearings);
        return "hearings/list";
    }

    @GetMapping("/cases/{caseId}/hearings/schedule")
    public String scheduleForm(@PathVariable Long caseId, Model model) {
        Optional<LegalCase> optCase = caseService.findById(caseId);
        if (optCase.isEmpty()) {
            return "redirect:/cases";
        }
        model.addAttribute("legalCase", optCase.get());
        model.addAttribute("caseId", caseId);
        return "hearings/schedule";
    }

    @PostMapping("/cases/{caseId}/hearings/schedule")
    public String scheduleHearing(@PathVariable Long caseId,
                                  @RequestParam String hearingDate,
                                  @RequestParam String location,
                                  @RequestParam String judgeName,
                                  RedirectAttributes redirectAttributes) {
        try {
            Optional<LegalCase> optCase = caseService.findById(caseId);
            if (optCase.isPresent()) {
                LocalDateTime dt = LocalDateTime.parse(hearingDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
                hearingService.scheduleHearing(optCase.get(), dt, location, judgeName);
                redirectAttributes.addFlashAttribute("success", "Hearing scheduled successfully!");
            } else {
                redirectAttributes.addFlashAttribute("error", "Case not found.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to schedule hearing: " + e.getMessage());
        }
        return "redirect:/cases/" + caseId;
    }

    @GetMapping("/hearings/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Optional<Hearing> optHearing = hearingService.findById(id);
        if (optHearing.isEmpty()) {
            return "redirect:/cases";
        }
        model.addAttribute("hearing", optHearing.get());
        return "hearings/edit";
    }

    @PostMapping("/hearings/{id}/reschedule")
    public String rescheduleHearing(@PathVariable Long id,
                                    @RequestParam String hearingDate,
                                    RedirectAttributes redirectAttributes) {
        try {
            LocalDateTime dt = LocalDateTime.parse(hearingDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            Hearing hearing = hearingService.reschedule(id, dt);
            redirectAttributes.addFlashAttribute("success", "Hearing rescheduled successfully!");
            return "redirect:/cases/" + hearing.getLegalCase().getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to reschedule hearing: " + e.getMessage());
            return "redirect:/cases";
        }
    }

    @PostMapping("/hearings/{id}/adjourn")
    public String adjournHearing(@PathVariable Long id,
                                 @RequestParam(defaultValue = "Adjourned") String reason,
                                 RedirectAttributes redirectAttributes) {
        try {
            Hearing hearing = hearingService.adjournHearing(id, reason);
            redirectAttributes.addFlashAttribute("success", "Hearing adjourned successfully!");
            return "redirect:/cases/" + hearing.getLegalCase().getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to adjourn hearing: " + e.getMessage());
            return "redirect:/cases";
        }
    }

    @PostMapping("/hearings/{id}/complete")
    public String completeHearing(@PathVariable Long id,
                                  @RequestParam(defaultValue = "Completed") String outcome,
                                  RedirectAttributes redirectAttributes) {
        try {
            Hearing hearing = hearingService.completeHearing(id, outcome);
            redirectAttributes.addFlashAttribute("success", "Hearing completed successfully!");
            return "redirect:/cases/" + hearing.getLegalCase().getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to complete hearing: " + e.getMessage());
            return "redirect:/cases";
        }
    }
}
