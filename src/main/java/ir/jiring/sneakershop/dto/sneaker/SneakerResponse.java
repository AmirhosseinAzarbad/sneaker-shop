package ir.jiring.sneakershop.dto.sneaker;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class SneakerResponse {
    private UUID id;
    private String name;
    private String brand;
    private BigDecimal price;
}
