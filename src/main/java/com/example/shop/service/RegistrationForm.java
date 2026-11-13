package com.example.shop.service;

import jakarta.validation.constraints.*;

public class RegistrationForm {
    @NotBlank(message = "Name is required") @Size(max = 80, message = "Name must be at most 80 characters")
    private String name;

    @NotBlank(message = "Email is required") @Email(message = "Enter a valid email address")
    @Size(max = 160, message = "Email must be at most 160 characters")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 72, message = "Password must be 8 to 72 characters")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).*$", message = "Password must contain a letter and a digit")
    private String password;

    @NotBlank(message = "Please confirm your password")
    private String confirmPassword;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
}
