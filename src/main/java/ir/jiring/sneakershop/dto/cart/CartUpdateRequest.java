package ir.jiring.sneakershop.dto.cart;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class CartUpdateRequest {
    @Min(value = 1, message = "quantity cant be zero or less")
    private Integer quantity;
}
