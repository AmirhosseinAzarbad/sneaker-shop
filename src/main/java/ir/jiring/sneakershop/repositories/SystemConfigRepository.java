package ir.jiring.sneakershop.repositories;


import ir.jiring.sneakershop.models.AdminRegPass;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SystemConfigRepository extends JpaRepository<AdminRegPass, Long> {
    Optional<AdminRegPass> findFirstByOrderByIdAsc();

}
