package com.rev.app.controller;

import com.rev.app.entity.User;
import com.rev.app.service.FollowService;
import com.rev.app.service.NotificationService;
import com.rev.app.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/follow")
public class FollowController {

    private final FollowService followService;
    private final UserService userService;
    private final NotificationService notificationService;

    public FollowController(FollowService followService, UserService userService,
            NotificationService notificationService) {
        this.followService = followService;
        this.userService = userService;
        this.notificationService = notificationService;
    }

    @PostMapping("/{userId}")
    public String follow(@PathVariable Long userId,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        User currentUser = userService.findByUsername(userDetails.getUsername());
        User target = userService.findById(userId);
        try {
            followService.follow(currentUser, target);
            redirectAttributes.addFlashAttribute("successMessage", "Now following " + target.getUsername());
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/profile/" + target.getUsername();
    }

    @PostMapping("/{userId}/unfollow")
    public String unfollow(@PathVariable Long userId,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {
        User currentUser = userService.findByUsername(userDetails.getUsername());
        User target = userService.findById(userId);
        followService.unfollow(currentUser, target);
        redirectAttributes.addFlashAttribute("successMessage", "Unfollowed " + target.getUsername());
        return "redirect:/profile/" + target.getUsername();
    }

    @GetMapping("/followers/{username}")
    public String followers(@PathVariable String username,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {
        User profileUser = userService.findByUsername(username);
        User currentUser = userService.findByUsername(userDetails.getUsername());
        model.addAttribute("users", followService.getFollowers(profileUser.getId()));
        model.addAttribute("title", "Followers of " + profileUser.getUsername());
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("unreadCount", notificationService.getUnreadCount(currentUser.getId()));
        return "user-list";
    }

    @GetMapping("/following/{username}")
    public String following(@PathVariable String username,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {
        User profileUser = userService.findByUsername(username);
        User currentUser = userService.findByUsername(userDetails.getUsername());
        model.addAttribute("users", followService.getFollowing(profileUser.getId()));
        model.addAttribute("title", "Following by " + profileUser.getUsername());
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("unreadCount", notificationService.getUnreadCount(currentUser.getId()));
        return "user-list";
    }
}
