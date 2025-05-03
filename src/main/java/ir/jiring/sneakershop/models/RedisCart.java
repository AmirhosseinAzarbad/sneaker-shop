package ir.jiring.sneakershop.models;

import ir.jiring.sneakershop.enums.CartStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class RedisCart implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull
    private UUID id = UUID.randomUUID();

    @NotNull
    private Long userId;

    @NotNull
    private CartStatus status = CartStatus.PENDING;

    private List<RedisCartItem> items = new ArrayList<>();

}
