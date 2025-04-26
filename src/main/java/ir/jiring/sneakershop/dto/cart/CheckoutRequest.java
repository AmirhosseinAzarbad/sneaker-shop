package ir.jiring.sneakershop.dto.cart;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CheckoutRequest {
    @NotBlank
    private String recipientName;

    @NotBlank
    private String recipientPhoneNumber;

    @NotBlank
    private String shippingAddress;

    @NotBlank
    @Pattern(regexp = "\\d{10}", message = "postalCode must be 10 numeric characters")
    private String shippingPostalCode;
}
