package ir.jiring.sneakershop.mapper;

import ir.jiring.sneakershop.dto.cart.CartItemResponse;
import ir.jiring.sneakershop.dto.cart.CartResponse;
import ir.jiring.sneakershop.models.*;
import ir.jiring.sneakershop.repositories.SneakerVariantRepository;
import ir.jiring.sneakershop.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;

import java.math.BigDecimal;
import java.util.stream.Collectors;

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

    public static RedisCart toRedisCart(Cart cart) {
        RedisCart rc = new RedisCart();
        rc.setId(cart.getId());
        rc.setUserId(cart.getUser().getUserId());
        rc.setStatus(cart.getStatus());
        rc.setItems(cart.getItems().stream().map(item -> {
            RedisCartItem ri = new RedisCartItem();
            ri.setId(item.getId());
            ri.setVariantId(item.getVariant().getId());
            ri.setQuantity(item.getQuantity());
            ri.setPriceAtTime(item.getPriceAtTime());
            ri.setPriceConfirmed(item.isPriceConfirmed());
            return ri;
        }).collect(Collectors.toList()));
        return rc;
    }

    public static Cart fromRedisCart(RedisCart rc,
                                     UserRepository userRepo,
                                     SneakerVariantRepository variantRepo) {
        Cart cart = new Cart();
        cart.setId(rc.getId());
        cart.setStatus(rc.getStatus());

        User user = userRepo.findById(rc.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User not found for id: " + rc.getUserId()));
        cart.setUser(user);

        cart.setItems(rc.getItems().stream().map(ri -> {
            CartItem item = new CartItem();
            item.setId(ri.getId());
            SneakerVariant variant = variantRepo.findById(ri.getVariantId())
                    .orElseThrow(() -> new EntityNotFoundException("Variant not found for id: " + ri.getVariantId()));
            item.setVariant(variant);
            item.setQuantity(ri.getQuantity());
            item.setPriceAtTime(ri.getPriceAtTime());
            item.setPriceConfirmed(ri.isPriceConfirmed());
            item.setCart(cart);
            return item;
        }).collect(Collectors.toList()));

        return cart;
    }
}
