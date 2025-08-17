package ir.jiring.sneakershop.repositories.jpa;

import ir.jiring.sneakershop.models.Sneaker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository("sneakerRepositoryJpa")
public interface SneakerRepositoryJpa extends JpaRepository<Sneaker, UUID> {
}
