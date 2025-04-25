package ir.jiring.sneakershop.repositories;

import ir.jiring.sneakershop.enums.Role;
import ir.jiring.sneakershop.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    boolean existsByRole(Role role);

    Optional<User> findByPhoneNumber(String phoneNumber);

    List<User> findByRole(Role role);
}