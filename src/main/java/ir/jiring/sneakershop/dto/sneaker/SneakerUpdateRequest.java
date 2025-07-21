package ir.jiring.sneakershop.dto.sneaker;

import jakarta.validation.constraints.DecimalMin;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SneakerUpdateRequest {

    private String name;

    private String brand;


    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal price;
}
