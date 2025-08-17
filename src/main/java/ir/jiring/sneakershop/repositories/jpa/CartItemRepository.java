package ir.jiring.sneakershop.repositories.jpa;

import ir.jiring.sneakershop.models.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}
