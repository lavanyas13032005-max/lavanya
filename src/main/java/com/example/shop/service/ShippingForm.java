package com.example.shop.service;

import jakarta.validation.constraints.*;

public class ShippingForm {
    @NotBlank(message = "Full name is required") @Size(max = 80, message = "At most 80 characters")
    private String fullName;

    @NotBlank(message = "Street address is required") @Size(max = 120, message = "At most 120 characters")
    private String addressLine;

    @NotBlank(message = "City is required") @Size(max = 60, message = "At most 60 characters")
    private String city;

    @NotBlank(message = "Postal code is required")
    @Pattern(regexp = "^[A-Za-z0-9][A-Za-z0-9 \\-]{2,9}$", message = "Enter a valid postal code")
    private String postalCode;

    @NotBlank(message = "Country is required") @Size(max = 60, message = "At most 60 characters")
    private String country;

    @Pattern(regexp = "^[0-9+()\\- ]{0,20}$", message = "Enter a valid phone number")
    private String phone;

    public String getFullName() { return fullName; }
    public void setFullName(String v) { this.fullName = v; }
    public String getAddressLine() { return addressLine; }
    public void setAddressLine(String v) { this.addressLine = v; }
    public String getCity() { return city; }
    public void setCity(String v) { this.city = v; }
    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String v) { this.postalCode = v; }
    public String getCountry() { return country; }
    public void setCountry(String v) { this.country = v; }
    public String getPhone() { return phone; }
    public void setPhone(String v) { this.phone = v; }

    /** Multi-line text stored on the order and shown on the receipt. */
    public String toAddressText() {
        StringBuilder sb = new StringBuilder();
        sb.append(fullName.trim()).append('\n')
          .append(addressLine.trim()).append('\n')
          .append(postalCode.trim()).append(' ').append(city.trim()).append('\n')
          .append(country.trim());
        if (phone != null && !phone.isBlank()) sb.append("\nPhone: ").append(phone.trim());
        return sb.toString();
    }
}
