package com.rev.app.controller;

import com.rev.app.entity.Message;
import com.rev.app.entity.User;
import com.rev.app.service.MessageService;
import com.rev.app.service.NotificationService;
import com.rev.app.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/messages")
public class MessageController {

    private final MessageService messageService;
    private final UserService userService;
    private final NotificationService notificationService;

    public MessageController(MessageService messageService, UserService userService,
            NotificationService notificationService) {
        this.messageService = messageService;
        this.userService = userService;
        this.notificationService = notificationService;
    }

    @GetMapping
    public String inbox(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User currentUser = userService.findByUsername(userDetails.getUsername());
        List<User> contacts = messageService.getContacts(currentUser.getId());

        model.addAttribute("contacts", contacts);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("unreadCount", notificationService.getUnreadCount(currentUser.getId()));
        model.addAttribute("messageUnreadCount", messageService.getUnreadCount(currentUser.getId()));

        return "messages/inbox";
    }

    @GetMapping("/{contactId}")
    public String conversation(@PathVariable Long contactId,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {
        User currentUser = userService.findByUsername(userDetails.getUsername());
        User contact = userService.findById(contactId);

        messageService.markAsRead(currentUser.getId(), contactId);
        List<Message> messages = messageService.getConversation(currentUser.getId(), contactId);

        model.addAttribute("contact", contact);
        model.addAttribute("messages", messages);
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("unreadCount", notificationService.getUnreadCount(currentUser.getId()));
        model.addAttribute("messageUnreadCount", messageService.getUnreadCount(currentUser.getId()));

        return "messages/conversation";
    }

    @PostMapping("/{contactId}/send")
    public String sendMessage(@PathVariable Long contactId,
            @RequestParam String content,
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.findByUsername(userDetails.getUsername());
        messageService.sendMessage(currentUser, contactId, content);
        return "redirect:/messages/" + contactId;
    }

    @PostMapping("/{contactId}/delete")
    public String deleteConversation(@PathVariable Long contactId,
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.findByUsername(userDetails.getUsername());
        messageService.deleteConversation(currentUser.getId(), contactId);
        return "redirect:/messages";
    }

    @PostMapping("/delete/{messageId}")
    public String deleteMessage(@PathVariable Long messageId,
            @RequestParam Long contactId,
            @AuthenticationPrincipal UserDetails userDetails) {
        User currentUser = userService.findByUsername(userDetails.getUsername());
        messageService.deleteMessage(messageId, currentUser.getId());
        return "redirect:/messages/" + contactId;
    }
}
