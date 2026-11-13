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
    public String login() { return "login"; }

    @GetMapping("/register")
    public String registerForm(@ModelAttribute("form") RegistrationForm form) { return "register"; }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("form") RegistrationForm form, BindingResult result) {
        if (!result.hasFieldErrors("email") && users.existsByEmailIgnoreCase(form.getEmail())) {
            result.rejectValue("email", "duplicate", "Email is already registered");
        }
        if (result.hasErrors()) return "register";
        UserAccount u = new UserAccount();
        u.setEmail(form.getEmail().trim());
        u.setName(form.getName().trim());
        u.setPasswordHash(encoder.encode(form.getPassword()));
        users.save(u);
        return "redirect:/login?registered";
    }
}
