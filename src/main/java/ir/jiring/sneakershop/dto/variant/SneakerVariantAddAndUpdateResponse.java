package ir.jiring.sneakershop.dto.variant;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class SneakerVariantAddAndUpdateResponse {
    private String sneakerName;

    private String sneakerBrand;

    private UUID id;

    private String color;

    private String size;

    private BigDecimal price;

    private Integer stockQuantity;

}

