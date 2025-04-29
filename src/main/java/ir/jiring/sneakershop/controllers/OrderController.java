package ir.jiring.sneakershop.controllers;

import ir.jiring.sneakershop.dto.order.OrderResponse;
import ir.jiring.sneakershop.dto.order.StatusUpdateRequest;
import ir.jiring.sneakershop.services.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }


//    @PostMapping
//    public ResponseEntity<OrderResponse> placeOrder(Authentication auth) {
//        String user = auth.getName();
//        OrderResponse res = orderService.placeOrder(user);
//        return ResponseEntity.status(HttpStatus.CREATED).body(res);
//    }

    @GetMapping("/delivered")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER')")
    public ResponseEntity<List<OrderResponse>> deliveredOrders() {
        return ResponseEntity.ok(orderService.listDeliveredOrders());
    }

    @GetMapping("/cancelled")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER')")
    public ResponseEntity<List<OrderResponse>> cancelledOrders() {
        return ResponseEntity.ok(orderService.listCancelledOrders());
    }

    @GetMapping("/active")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER')")
    public ResponseEntity<List<OrderResponse>> activeOrders() {
        return ResponseEntity.ok(orderService.listActiveOrders());
    }

    @GetMapping("/my")
    public ResponseEntity<List<OrderResponse>> userOrders(Authentication auth) {
        return ResponseEntity.ok(orderService.userOrders(auth.getName()));
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public OrderResponse changeStatus(
            @PathVariable UUID id,
            @Valid @RequestBody StatusUpdateRequest req
    ) {
        return orderService.updateStatus(id, req.getStatus());
    }
}

