package ir.jiring.sneakershop.mapper;

import ir.jiring.sneakershop.dto.cart.CartItemResponse;
import ir.jiring.sneakershop.dto.cart.CartResponse;
import ir.jiring.sneakershop.models.Cart;
import ir.jiring.sneakershop.models.CartItem;

import java.math.BigDecimal;

public class CartMapper {

    public static CartItemResponse toItemResponse(CartItem item) {
        CartItemResponse res = new CartItemResponse();
        res.setItemId(item.getId());
        res.setSneakerName(item.getVariant().getSneaker().getName());
        res.setColor(item.getVariant().getColor());
        res.setSize(item.getVariant().getSize());
        res.setQuantity(item.getQuantity());
        res.setUnitPrice(item.getPriceAtTime());
        res.setTotalPrice(item.getPriceAtTime().multiply(BigDecimal.valueOf(item.getQuantity())));
        res.setPriceConfirmed(item.isPriceConfirmed());
        return res;
    }

    public static CartResponse toCartResponse(Cart cart) {
        CartResponse res = new CartResponse();
        res.setItems(cart.getItems().stream().map(CartMapper::toItemResponse).toList());
        res.setTotalAmount(cart.getTotalAmount());
        res.setStatus(cart.getStatus().name());
        return res;
    }
}
