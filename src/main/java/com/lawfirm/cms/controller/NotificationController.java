package com.lawfirm.cms.controller;

import com.lawfirm.cms.model.User;
import com.lawfirm.cms.service.NotificationQueryService;
import com.lawfirm.cms.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.Optional;

@Controller
public class NotificationController {

    private final NotificationQueryService notificationQueryService;
    private final UserService userService;

    public NotificationController(NotificationQueryService notificationQueryService,
                                  UserService userService) {
        this.notificationQueryService = notificationQueryService;
        this.userService = userService;
    }

    @GetMapping("/notifications")
    public String listNotifications(Model model, Principal principal) {
        Optional<User> optUser = userService.findByEmail(principal.getName());
        if (optUser.isEmpty()) {
            return "redirect:/login";
        }
        User user = optUser.get();
        model.addAttribute("notifications", notificationQueryService.getNotifications(user.getId()));
        model.addAttribute("user", user);
        return "notifications";
    }

    @PostMapping("/notifications/{id}/read")
    public String markAsRead(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        notificationQueryService.markAsRead(id);
        redirectAttributes.addFlashAttribute("success", "Notification marked as read.");
        return "redirect:/notifications";
    }
}
