package ir.jiring.sneakershop.dto.cart;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CartItemResponse {
    private UUID itemId;
    private String sneakerName;
    private String color;
    private String size;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private boolean priceConfirmed;

}