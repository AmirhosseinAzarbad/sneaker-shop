package ir.jiring.sneakershop.repositories;

import ir.jiring.sneakershop.models.Sneaker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SneakerRepository extends JpaRepository<Sneaker, UUID> {
}
