package ir.jiring.sneakershop.dto.variant;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SneakerVariantAddRequest {


    @NotBlank(message = "size cannot be Blank")
    private String size;

    @NotBlank(message = "color cannot be Blank")
    private String color;

    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal price;

    @Min(value = 0,message = "stockQuantity cannot be negative")
    private Integer stockQuantity;
}
