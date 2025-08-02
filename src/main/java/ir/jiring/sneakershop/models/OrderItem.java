package ir.jiring.sneakershop.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name="order_items")
@Data
@NoArgsConstructor
public class OrderItem {
    @Id
    private UUID id = UUID.randomUUID();

    @ManyToOne(optional=false)
    private Order order;

    @ManyToOne(optional=false)
    private SneakerVariant variant;

    private int quantity;

    private BigDecimal priceAtOrder;
}
