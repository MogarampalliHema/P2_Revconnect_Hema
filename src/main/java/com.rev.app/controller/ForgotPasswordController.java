package com.rev.app.controller;

import com.rev.app.entity.PasswordResetToken;
import com.rev.app.entity.User;
import com.rev.app.repository.PasswordResetTokenRepository;
import com.rev.app.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/forgot-password")
public class ForgotPasswordController {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;

    public ForgotPasswordController(UserRepository userRepository,
                                    PasswordResetTokenRepository tokenRepository,
                                    PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String showForgotPasswordForm() {
        return "auth/forgot-password";
    }

    @PostMapping
    public String processForgotPassword(@RequestParam String usernameOrEmail, RedirectAttributes redirectAttributes) {
        Optional<User> userOptional = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            String token = UUID.randomUUID().toString();

            // Delete existing token if any
            tokenRepository.findByUser(user).ifPresent(tokenRepository::delete);

            PasswordResetToken resetToken = new PasswordResetToken(token, user);
            tokenRepository.save(resetToken);

            // In a real app, send email here. For now, we simulate.
            System.out.println("Password reset link: http://localhost:8080/forgot-password/reset?token=" + token);

            redirectAttributes.addFlashAttribute("successMessage",
                    "A reset link has been processed. We are redirecting you straight to the reset page.");
            // For convenience in testing, let's redirect to the reset page with token
            return "redirect:/forgot-password/reset?token=" + token;
        } else {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "No account found with that username or email address.");
            return "redirect:/forgot-password";
        }
    }

    @GetMapping("/reset")
    public String showResetPasswordForm(@RequestParam String token, Model model) {
        Optional<PasswordResetToken> resetTokenOptional = tokenRepository.findByToken(token);
        if (resetTokenOptional.isEmpty() || resetTokenOptional.get().isExpired()) {
            model.addAttribute("errorMessage", "Invalid or expired token.");
            return "auth/forgot-password";
        }
        model.addAttribute("token", token);
        return "auth/reset-password";
    }

    @PostMapping("/reset")
    public String processResetPassword(@RequestParam String token, @RequestParam String password,
                                       RedirectAttributes redirectAttributes) {
        Optional<PasswordResetToken> resetTokenOptional = tokenRepository.findByToken(token);
        if (resetTokenOptional.isEmpty() || resetTokenOptional.get().isExpired()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid or expired token.");
            return "redirect:/forgot-password";
        }

        User user = resetTokenOptional.get().getUser();
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);

        tokenRepository.delete(resetTokenOptional.get());

        redirectAttributes.addFlashAttribute("successMessage", "Password has been successfully reset! Please login.");
        return "redirect:/login";
    }
}
