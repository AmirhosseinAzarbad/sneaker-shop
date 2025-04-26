package ir.jiring.sneakershop.repositories;

import ir.jiring.sneakershop.models.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CartRepository extends JpaRepository<Cart, UUID> {

}
