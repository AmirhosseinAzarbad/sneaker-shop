package ir.jiring.sneakershop.dto.order;

import ir.jiring.sneakershop.dto.cart.CartItemResponse;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class OrderResponse {
    private UUID orderId;
    private List<CartItemResponse> items;
    private BigDecimal totalAmount;
    private String status;
    private LocalDateTime createdAt;
    private String recipientName;
    private String recipientPhoneNumber;
    private String shippingAddress;
    private String shippingPostalCode;
}
