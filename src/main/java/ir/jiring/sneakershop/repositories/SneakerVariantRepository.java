package ir.jiring.sneakershop.repositories;

import ir.jiring.sneakershop.models.SneakerVariant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SneakerVariantRepository extends JpaRepository<SneakerVariant, Long> {

    List<SneakerVariant> findAllBySneakerId(UUID sneakerId);

    Optional<SneakerVariant> findById(UUID id);
}
