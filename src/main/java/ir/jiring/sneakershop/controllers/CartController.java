package ir.jiring.sneakershop.controllers;

import ir.jiring.sneakershop.dto.cart.AddToCartRequest;
import ir.jiring.sneakershop.dto.cart.CartResponse;
import ir.jiring.sneakershop.dto.cart.CartUpdateRequest;
import ir.jiring.sneakershop.dto.cart.CheckoutRequest;
import ir.jiring.sneakershop.services.CartService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("show")
    public ResponseEntity<CartResponse> getCart(Authentication authentication) {
        return ResponseEntity.ok(cartService.getOrCreateCartResponse(authentication.getName()));
    }


    @PostMapping("/add")
    public ResponseEntity<CartResponse> addToCart(@Valid@RequestBody AddToCartRequest request, Authentication authentication) {
        return ResponseEntity.ok(cartService.addToCart(authentication.getName(), request));
    }

    @PutMapping("/item/{itemId}")
    public ResponseEntity<CartResponse> updateCartItem(@PathVariable UUID itemId,
                                                       @Valid @RequestBody CartUpdateRequest request,
                                                       Authentication authentication) {
        return ResponseEntity.ok(cartService.updateCartItem(authentication.getName(), itemId, request));
    }

    @DeleteMapping("/item/{itemId}")
    public ResponseEntity<CartResponse> removeCartItem(@PathVariable UUID itemId,
                                               Authentication authentication) {
        return ResponseEntity.ok(cartService.removeCartItem(authentication.getName(), itemId));
    }

    @PostMapping("/checkout")
    public ResponseEntity<CartResponse> checkout(Authentication authentication,@Valid @RequestBody CheckoutRequest request) {
                    return ResponseEntity.ok(cartService.checkout(authentication.getName(), request));
    }

    @PutMapping("/item/{itemId}/confirm-price")
    public ResponseEntity<CartResponse> confirmItemPrice(@PathVariable UUID itemId,
                                                         Authentication authentication) {
        return ResponseEntity.ok(cartService.confirmItemPrice(authentication.getName(), itemId));
    }
}
