package ir.jiring.sneakershop.services;

import ir.jiring.sneakershop.dto.order.OrderResponse;
import ir.jiring.sneakershop.enums.CartStatus;
import ir.jiring.sneakershop.enums.OrderStatus;
import ir.jiring.sneakershop.mapper.OrderMapper;
import ir.jiring.sneakershop.models.Cart;
import ir.jiring.sneakershop.models.Order;
import ir.jiring.sneakershop.repositories.CartRepository;
import ir.jiring.sneakershop.repositories.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepo;
    private final CartRepository cartRepo;
    private final CartRedisService cartRedisService;


    @Transactional
    public void placeOrder(String username) {

        Cart cart = cartRepo.findFirstByUserUsernameAndStatusOrderByCreatedAtDesc(username, CartStatus.CHECKED_OUT)
                .orElseThrow(() -> new EntityNotFoundException("Cart not found"));


        Order order = OrderMapper.mapCartToOrder(cart);
        orderRepo.save(order);

        cartRedisService.deleteAfterCheckout(username);

    }



    @Transactional(readOnly = true)
    public List<OrderResponse> listDeliveredOrders() {
        List<Order> orders = orderRepo.findAllByStatus(OrderStatus.DELIVERED);
        return orders.stream()
                .map(OrderMapper::toOrderResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> listCancelledOrders() {
        List<Order> orders = orderRepo.findAllByStatus(OrderStatus.CANCELLED);
        return orders.stream()
                .map(OrderMapper::toOrderResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> listActiveOrders() {
        List<Order> orders = orderRepo.findAllByStatusNotIn(List.of(OrderStatus.CANCELLED , OrderStatus.DELIVERED));
        return orders.stream()
                .map(OrderMapper::toOrderResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> userOrders(String username) {
        List<Order> orders = orderRepo.findByUserUsername(username);
        return orders.stream()
                .map(OrderMapper::toOrderResponse)
                .toList();
    }


    @Transactional
    public OrderResponse updateStatus(UUID orderId, OrderStatus status) {
        Order order = orderRepo.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order not found"));
        order.setStatus(status);
        return OrderMapper.toOrderResponse(orderRepo.save(order));
    }
}

