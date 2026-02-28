package com.rev.app.controller;

import com.rev.app.dto.RegisterDTO;
import com.rev.app.entity.User;
import com.rev.app.exception.UserAlreadyExistsException;
import com.rev.app.service.UserService;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private static final Logger logger = LogManager.getLogger(AuthController.class);

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String home(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/feed";
        }
        return "index";
    }

    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String error,
            @RequestParam(required = false) String logout,
            Model model) {
        if (error != null)
            model.addAttribute("errorMessage", "Invalid username/email or password.");
        if (logout != null)
            model.addAttribute("successMessage", "You have been logged out successfully.");
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("registerDTO", new RegisterDTO());
        model.addAttribute("roles", User.UserRole.values());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registerDTO") RegisterDTO dto,
            BindingResult result,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("roles", User.UserRole.values());
            return "auth/register";
        }
        try {
            userService.register(dto);
            logger.info("AuthController: Registration successful for user: {}. Redirecting to login.",
                    dto.getUsername());
            redirectAttributes.addFlashAttribute("successMessage", "Account created! Please log in.");
            return "redirect:/login";
        } catch (UserAlreadyExistsException ex) {
            logger.warn("AuthController: Registration failed - user already exists: {}", ex.getMessage());
            model.addAttribute("errorMessage", ex.getMessage());
            model.addAttribute("roles", User.UserRole.values());
            return "auth/register";
        }
    }
}
