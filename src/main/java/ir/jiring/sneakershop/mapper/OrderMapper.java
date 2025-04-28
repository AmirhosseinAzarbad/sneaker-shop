package ir.jiring.sneakershop.mapper;

import ir.jiring.sneakershop.dto.cart.CartItemResponse;
import ir.jiring.sneakershop.dto.order.OrderResponse;
import ir.jiring.sneakershop.enums.OrderStatus;
import ir.jiring.sneakershop.models.Cart;
import ir.jiring.sneakershop.models.Order;
import ir.jiring.sneakershop.models.OrderItem;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrderMapper {

    public static Order mapCartToOrder(Cart cart) {
        Order o = new Order();
        o.setUser(cart.getUser());
        o.setStatus(OrderStatus.PENDING);
        o.setCreatedAt(LocalDateTime.now());
        o.setRecipientName(cart.getRecipientName());
        o.setRecipientPhoneNumber(cart.getRecipientPhoneNumber());
        o.setShippingAddress(cart.getShippingAddress());
        o.setShippingPostalCode(cart.getShippingPostalCode());

        cart.getItems().forEach(ci -> {
            OrderItem oi = new OrderItem();
            oi.setOrder(o);
            oi.setVariant(ci.getVariant());
            oi.setQuantity(ci.getQuantity());
            oi.setPriceAtOrder(ci.getPriceAtTime());
            o.getItems().add(oi);
        });
        return o;
    }

    public static OrderResponse toOrderResponse(Order o) {
        OrderResponse res = new OrderResponse();
        res.setOrderId(o.getId());
        res.setStatus(o.getStatus().name());
        res.setCreatedAt(o.getCreatedAt());
        res.setTotalAmount(o.getTotalAmount());
        res.setRecipientName(o.getRecipientName());
        res.setRecipientPhoneNumber(o.getRecipientPhoneNumber());
        res.setShippingAddress(o.getShippingAddress());
        res.setShippingPostalCode(o.getShippingPostalCode());

        res.setItems(o.getItems().stream()
                .map(OrderMapper::toOrderItemResponse)
                .toList());
        return res;
    }

    private static CartItemResponse toOrderItemResponse(OrderItem oi) {
        CartItemResponse res = new CartItemResponse();
        res.setItemId(oi.getId());
        res.setSneakerName(oi.getVariant().getSneaker().getName());
        res.setColor(oi.getVariant().getColor());
        res.setSize(oi.getVariant().getSize());
        res.setQuantity(oi.getQuantity());
        res.setUnitPrice(oi.getPriceAtOrder());
        res.setTotalPrice(oi.getPriceAtOrder().multiply(BigDecimal.valueOf(oi.getQuantity())));
        res.setPriceConfirmed(true);
        return res;
    }
}
