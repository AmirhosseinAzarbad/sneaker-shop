package ir.jiring.sneakershop.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class CartItem implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private UUID id = UUID.randomUUID();

    @ManyToOne(optional = false)
    private Cart cart;

    @ManyToOne(optional = false)
    private SneakerVariant variant;

    private int quantity;

    private BigDecimal priceAtTime;

    @Column(nullable = false)
    private boolean priceConfirmed = true;

}
