package com.example.shop.web;

import com.example.shop.model.UserAccount;
import com.example.shop.repository.UserRepository;
import com.example.shop.service.RegistrationForm;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {
    private final UserRepository users;
    private final PasswordEncoder encoder;

    public AuthController(UserRepository users, PasswordEncoder encoder) {
        this.users = users;
        this.encoder = encoder;
    }

    @GetMapping("/login")
    public String login(org.springframework.security.core.Authentication auth) {
        boolean signedIn = auth != null && auth.isAuthenticated()
                && !(auth instanceof org.springframework.security.authentication.AnonymousAuthenticationToken);
        return signedIn ? "redirect:/" : "login";
    }

    @GetMapping("/register")
    public String registerForm(@ModelAttribute("form") RegistrationForm form) { return "register"; }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("form") RegistrationForm form, BindingResult result) {
        if (!result.hasFieldErrors("password") && !result.hasFieldErrors("confirmPassword")
                && !form.getPassword().equals(form.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "mismatch", "Passwords do not match");
        }
        if (!result.hasFieldErrors("email") && users.existsByEmailIgnoreCase(form.getEmail().trim())) {
            result.rejectValue("email", "duplicate", "Email is already registered");
        }
        if (result.hasErrors()) return "register";
        UserAccount u = new UserAccount();
        u.setEmail(form.getEmail().trim().toLowerCase(java.util.Locale.ROOT));
        u.setName(form.getName().trim());
        u.setPasswordHash(encoder.encode(form.getPassword()));
        try {
            users.save(u);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            result.rejectValue("email", "duplicate", "Email is already registered");
            return "register";
        }
        return "redirect:/login?registered";
    }
}
