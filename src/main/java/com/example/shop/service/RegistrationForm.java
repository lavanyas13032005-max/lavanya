package com.example.shop.service;

import jakarta.validation.constraints.*;

public class RegistrationForm {
    @NotBlank @Size(max = 80)
    private String name;
    @NotBlank @Email @Size(max = 160)
    private String email;
    @NotBlank @Size(min = 8, max = 72)
    private String password;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
