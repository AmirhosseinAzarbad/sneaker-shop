package ir.jiring.sneakershop.dto.cart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class AddToCartRequest {
    @NotNull(message = "Sneaker id cant be null")
    private UUID sneakerId;

    @Min(value = 1,message = "quantity cant be zero or less")
    private Integer quantity;
    @NotBlank
    private String selectedSize;
    @NotBlank
    private String selectedColor;
}
