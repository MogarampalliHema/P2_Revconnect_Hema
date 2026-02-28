package com.rev.app.controller;

import com.rev.app.dto.NotificationPreferenceDTO;
import com.rev.app.entity.NotificationPreference;
import com.rev.app.entity.User;
import com.rev.app.repository.NotificationPreferenceRepository;
import com.rev.app.service.NotificationService;
import com.rev.app.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService userService;
    private final NotificationPreferenceRepository preferenceRepository;

    public NotificationController(NotificationService notificationService,
            UserService userService,
            NotificationPreferenceRepository preferenceRepository) {
        this.notificationService = notificationService;
        this.userService = userService;
        this.preferenceRepository = preferenceRepository;
    }

    @GetMapping
    public String notifications(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User currentUser = userService.findByUsername(userDetails.getUsername());
        model.addAttribute("notifications", notificationService.getNotifications(currentUser.getId()));
        model.addAttribute("unreadCount", notificationService.getUnreadCount(currentUser.getId()));
        model.addAttribute("currentUser", currentUser);
        notificationService.markAllAsRead(currentUser.getId());
        return "notifications";
    }

    @PostMapping("/read/{id}")
    public String markRead(@PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        notificationService.markAsRead(id);
        return "redirect:/notifications";
    }

    @GetMapping("/preferences")
    public String preferencesForm(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User currentUser = userService.findByUsername(userDetails.getUsername());
        NotificationPreference prefs = preferenceRepository.findByUserId(currentUser.getId())
                .orElseGet(() -> new NotificationPreference(currentUser));
        NotificationPreferenceDTO dto = new NotificationPreferenceDTO();
        dto.setConnectionRequests(prefs.isConnectionRequests());
        dto.setConnectionAccepted(prefs.isConnectionAccepted());
        dto.setNewFollowers(prefs.isNewFollowers());
        dto.setPostLikes(prefs.isPostLikes());
        dto.setPostComments(prefs.isPostComments());
        dto.setPostShares(prefs.isPostShares());
        model.addAttribute("prefsDTO", dto);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("unreadCount", notificationService.getUnreadCount(currentUser.getId()));
        return "notification-preferences";
    }

    @PostMapping("/preferences")
    public String updatePreferences(@AuthenticationPrincipal UserDetails userDetails,
            @ModelAttribute NotificationPreferenceDTO dto,
            RedirectAttributes redirectAttributes) {
        User currentUser = userService.findByUsername(userDetails.getUsername());
        NotificationPreference prefs = preferenceRepository.findByUserId(currentUser.getId())
                .orElseGet(() -> new NotificationPreference(currentUser));
        prefs.setConnectionRequests(dto.isConnectionRequests());
        prefs.setConnectionAccepted(dto.isConnectionAccepted());
        prefs.setNewFollowers(dto.isNewFollowers());
        prefs.setPostLikes(dto.isPostLikes());
        prefs.setPostComments(dto.isPostComments());
        prefs.setPostShares(dto.isPostShares());
        preferenceRepository.save(prefs);
        redirectAttributes.addFlashAttribute("successMessage", "Preferences saved!");
        return "redirect:/notifications/preferences";
    }

    @PostMapping("/delete/{id}")
    public String deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return "redirect:/notifications";
    }

    @PostMapping("/clear")
    public String clearNotifications(@AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.findByUsername(userDetails.getUsername());
        notificationService.clearAllNotifications(currentUser.getId());
        return "redirect:/notifications";
    }
}
