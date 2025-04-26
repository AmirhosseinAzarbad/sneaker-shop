package ir.jiring.sneakershop.models;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@Data
public class RedisCartItem implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull
    private UUID id = UUID.randomUUID();

    @NotNull
    private UUID variantId;

    @Min(1)
    private int quantity;

    @NotNull
    private BigDecimal priceAtTime;

    private boolean priceConfirmed = false;
}
