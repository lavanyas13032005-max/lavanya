package com.example.shop.service;

import jakarta.validation.constraints.*;
import java.time.YearMonth;

/** Card fields from the checkout form. Held only for the duration of the request. */
public class CardForm {
    @NotBlank(message = "Card number is required")
    @Pattern(regexp = "^\\d{13,19}$", message = "Enter a valid card number")
    private String number;

    @NotBlank(message = "Name on card is required") @Size(max = 80, message = "At most 80 characters")
    private String holderName;

    @NotBlank(message = "Expiry date is required")
    @Pattern(regexp = "^(0[1-9]|1[0-2])/\\d{2}$", message = "Use MM/YY")
    private String expiry;

    @NotBlank(message = "Security code is required")
    @Pattern(regexp = "^\\d{3,4}$", message = "Enter the 3 or 4 digit code")
    private String cvc;

    public String getNumber() { return number; }
    public void setNumber(String number) { this.number = number == null ? null : number.replaceAll("[ -]", ""); }
    public String getHolderName() { return holderName; }
    public void setHolderName(String holderName) { this.holderName = holderName; }
    public String getExpiry() { return expiry; }
    public void setExpiry(String expiry) { this.expiry = expiry == null ? null : expiry.trim(); }
    public String getCvc() { return cvc; }
    public void setCvc(String cvc) { this.cvc = cvc; }

    /** Standard Luhn checksum, catches mistyped numbers before the gateway is called. */
    public boolean hasValidChecksum() {
        int sum = 0;
        boolean dbl = false;
        for (int i = number.length() - 1; i >= 0; i--) {
            int d = number.charAt(i) - '0';
            if (dbl && (d *= 2) > 9) d -= 9;
            sum += d;
            dbl = !dbl;
        }
        return sum % 10 == 0;
    }

    public boolean isExpired() {
        int month = Integer.parseInt(expiry.substring(0, 2));
        int year = 2000 + Integer.parseInt(expiry.substring(3));
        return YearMonth.of(year, month).isBefore(YearMonth.now());
    }

    public CardDetails toDetails() {
        return new CardDetails(number, holderName.trim(), Integer.parseInt(expiry.substring(0, 2)),
                2000 + Integer.parseInt(expiry.substring(3)), cvc);
    }
}
