package ir.jiring.sneakershop.repositories;

import org.springframework.data.repository.query.Param;
import ir.jiring.sneakershop.models.SneakerVariant;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface SneakerVariantRepository extends JpaRepository<SneakerVariant, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT v FROM SneakerVariant v WHERE v.id = :id")
    Optional<SneakerVariant> findByIdForUpdate(@Param("id") UUID id);

    Optional<SneakerVariant> findBySneakerIdAndSizeAndColor(UUID sneakerId, String size, String color);

    List<SneakerVariant> findAllBySneakerId(UUID sneakerId);

    Optional<SneakerVariant> findById(UUID id);
}
