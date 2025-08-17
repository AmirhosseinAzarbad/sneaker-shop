package ir.jiring.sneakershop.repositories.jpa;

import ir.jiring.sneakershop.enums.CartStatus;
import ir.jiring.sneakershop.models.Cart;
import ir.jiring.sneakershop.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CartRepository extends JpaRepository<Cart, UUID> {
    Optional<Cart> findByUserAndStatus(User user, CartStatus status);

    List<Cart> findAllByStatusAndCreatedAtBefore(CartStatus status, LocalDateTime before);

    Optional<Cart> findFirstByUserUsernameAndStatusOrderByCreatedAtDesc(String username, CartStatus status);
}
