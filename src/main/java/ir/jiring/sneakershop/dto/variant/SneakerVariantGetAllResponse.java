package ir.jiring.sneakershop.dto.variant;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
public class SneakerVariantGetAllResponse {

    private String sneakerName;
    private String sneakerBrand;

    private List<VariantsList> variants;

    @Data
    public static class VariantsList {
        private UUID id;
        private String color;
        private String size;
        private Integer stockQuantity;
        private BigDecimal price;
    }
}
