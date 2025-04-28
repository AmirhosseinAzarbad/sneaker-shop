package ir.jiring.sneakershop.repositories;

import ir.jiring.sneakershop.enums.OrderStatus;
import ir.jiring.sneakershop.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByUserUsername(String username);

    List<Order> findAllByStatus(OrderStatus status);

    List<Order> findAllByStatusNotIn(Collection<OrderStatus> statuses);
}
