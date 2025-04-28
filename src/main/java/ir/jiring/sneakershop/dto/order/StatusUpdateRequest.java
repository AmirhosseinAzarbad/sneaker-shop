package ir.jiring.sneakershop.dto.order;

import ir.jiring.sneakershop.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StatusUpdateRequest {
    @NotNull
    private OrderStatus status;
}
