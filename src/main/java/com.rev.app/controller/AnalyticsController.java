package com.rev.app.controller;

import com.rev.app.entity.User;
import com.rev.app.service.AnalyticsService;
import com.rev.app.service.NotificationService;
import com.rev.app.service.UserService;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;
    private final UserService userService;
    private final NotificationService notificationService;

    public AnalyticsController(AnalyticsService analyticsService,
            UserService userService,
            NotificationService notificationService) {
        this.analyticsService = analyticsService;
        this.userService = userService;
        this.notificationService = notificationService;
    }

    @GetMapping
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User currentUser = userService.findByUsername(userDetails.getUsername());
        model.addAttribute("postAnalytics", analyticsService.getPostAnalytics(currentUser.getId()));
        model.addAttribute("metrics", analyticsService.getAccountMetrics(currentUser.getId()));
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("unreadCount", notificationService.getUnreadCount(currentUser.getId()));
        return "analytics";
    }
}
