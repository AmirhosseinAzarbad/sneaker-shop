package ir.jiring.sneakershop.services;

import ir.jiring.sneakershop.dto.cart.AddToCartRequest;
import ir.jiring.sneakershop.dto.cart.CartResponse;
import ir.jiring.sneakershop.dto.cart.CartUpdateRequest;
import ir.jiring.sneakershop.dto.cart.CheckoutRequest;
import ir.jiring.sneakershop.enums.CartStatus;
import ir.jiring.sneakershop.exceptions.EmptyCartException;
import ir.jiring.sneakershop.exceptions.PaymentFailedException;
import ir.jiring.sneakershop.exceptions.PriceMismatchException;
import ir.jiring.sneakershop.mapper.CartMapper;
import ir.jiring.sneakershop.models.*;
import ir.jiring.sneakershop.repositories.CartItemRepository;
import ir.jiring.sneakershop.repositories.CartRepository;
import ir.jiring.sneakershop.repositories.SneakerVariantRepository;
import ir.jiring.sneakershop.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
public class CartService {

    private final CartRedisService cartRedisService;
    private final UserRepository userRepo;
    private final SneakerVariantRepository variantRepo;
    private final CartRepository cartRepo;
    private final CartItemRepository cartItemRepo;
    private final StockManager stockManager;

    public CartService(CartRedisService cartRedisService,
                       UserRepository userRepo,
                       SneakerVariantRepository variantRepo,
                       CartRepository cartRepo,
                       CartItemRepository cartItemRepo,
                       StockManager stockManager) {
        this.cartRedisService = cartRedisService;
        this.userRepo = userRepo;
        this.variantRepo = variantRepo;
        this.cartRepo = cartRepo;
        this.cartItemRepo = cartItemRepo;
        this.stockManager = stockManager;
    }

    public CartResponse getOrCreateCartResponse(String username) {
        Cart cart = getOrCreateCart(username);
        return CartMapper.toCartResponse(cart);
    }

    public Cart getOrCreateCart(String username) {
        return cartRedisService.getRedisCart(username)
                .filter(cart -> cart.getStatus() == CartStatus.ACTIVE)
                .orElseGet(() -> {
                    User user = userRepo.findByUsername(username)
                            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
                    Cart cart = new Cart();
                    cart.setUser(user);
                    cart.setStatus(CartStatus.ACTIVE);
                    cartRedisService.saveRedisCart(username, cart);
                    return cart;
                });
    }

    @Transactional
    public CartResponse addToCart(String username, AddToCartRequest request) {
        Cart cart = getOrCreateCart(username);

        SneakerVariant variant = variantRepo.findBySneakerIdAndSizeAndColor(
                        request.getSneakerId(), request.getSelectedSize(), request.getSelectedColor())
                .orElseThrow(() -> new EntityNotFoundException("Selected variant not found"));

        stockManager.reserve(variant.getId(), request.getQuantity());


        CartItem item = cart.getItems().stream()
                .filter(ci -> ci.getVariant() != null && ci.getVariant().getId().equals(variant.getId()))
                .findFirst()
                .orElseGet(() -> {
                    CartItem newItem = new CartItem();
                    newItem.setId(UUID.randomUUID());
                    newItem.setCart(cart);
                    newItem.setVariant(variant);
                    newItem.setQuantity(0);
                    newItem.setPriceAtTime(variant.getPrice() != null ? variant.getPrice() : variant.getSneaker().getPrice());
                    cart.getItems().add(newItem);

                    return newItem;
                });
        item.setQuantity(item.getQuantity() + request.getQuantity());


        cartRedisService.saveRedisCart(username, cart);
        return CartMapper.toCartResponse(cart);
    }

    public CartResponse updateCartItem(String username, UUID cartItemId, CartUpdateRequest request) {
        if (request.getQuantity() < 1)
            return removeCartItem(username, cartItemId);
        Cart cart = getOrCreateCart(username);

        CartItem item = cart.getItems().stream()
                .filter(i -> i.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Cart item not found"));

        if (item.getVariant() == null) {
            throw new EntityNotFoundException("CartItem has no variant!");
        }

        if (!cart.getUser().getUsername().equals(username)) {
            throw new AccessDeniedException("Access Denied");
        }

        int oldQty = item.getQuantity();
        int newQty = request.getQuantity();
        int delta = newQty - oldQty;

        if (delta > 0) stockManager.reserve(item.getVariant().getId(), delta);
        else if (delta < 0) stockManager.release(item.getVariant().getId(), -delta);

        item.setQuantity(newQty);
        cartRedisService.saveRedisCart(username, cart);
        return CartMapper.toCartResponse(cart);
    }

    public CartResponse removeCartItem(String username, UUID cartItemId) {
        Cart cart = getOrCreateCart(username);

        CartItem item = cart.getItems().stream()
                .filter(i -> i.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Cart item not found"));

        if (!cart.getUser().getUsername().equals(username)) {
            throw new AccessDeniedException("Access Denied");
        }

        stockManager.release(item.getVariant().getId(), item.getQuantity());

        cart.getItems().remove(item);
        cartItemRepo.delete(item);
        cartRedisService.saveRedisCart(username, cart);
        return CartMapper.toCartResponse(cart);
    }

    @Transactional
    public CartResponse checkout(String username, CheckoutRequest request) {
        Cart cart = getOrCreateCart(username);

        if (cart.getItems().isEmpty()) {
            throw new EmptyCartException("Cart is empty!");
        }

        cartRedisService.saveRedisCart(username, cart);

        List<CartItem> mismatchedItems = new ArrayList<>();

        for (CartItem item : cart.getItems()) {
            SneakerVariant variant = item.getVariant();
            BigDecimal currentPrice = Optional.ofNullable(variant.getPrice())
                    .orElse(variant.getSneaker().getPrice());
            if (item.getPriceAtTime().compareTo(currentPrice) != 0) {
                item.setPriceConfirmed(false);
                mismatchedItems.add(item);
            }
        }

        if (!mismatchedItems.isEmpty()) {
            throw new PriceMismatchException("Some items have changed price", mismatchedItems);
        }

        cart.setRecipientName(request.getRecipientName());
        cart.setShippingAddress(request.getShippingAddress());
        cart.setRecipientPhoneNumber(request.getRecipientPhoneNumber());
        cart.setShippingPostalCode(request.getShippingPostalCode());

        cartRedisService.saveRedisCart(username, cart);
        boolean paymentSuccess = new Random().nextDouble() < 0.8;
        if (!paymentSuccess) {
            throw new PaymentFailedException("Payment failed. please try again");
        }

        try {
            cart.setStatus(CartStatus.CHECKED_OUT);
            cartRedisService.deleteAfterCheckout(username);
            Cart savedCart = cartRepo.save(cart);

//       cartItemRepo.saveAll(savedCart.getItems());

            return CartMapper.toCartResponse(savedCart);
        } catch (Exception e) {
            stockManager.releaseCart(cart);
            throw e;
        }
    }

    public CartResponse confirmItemPrice(String username, UUID cartItemId) {
        Cart cart = getOrCreateCart(username);

        CartItem item = cart.getItems().stream()
                .filter(i -> i.getId().equals(cartItemId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Cart item not found"));

        if (!cart.getUser().getUsername().equals(username)) {
            throw new AccessDeniedException("Access Denied");
        }

        SneakerVariant variant = item.getVariant();
        BigDecimal currentPrice = variant.getPrice() != null
                ? variant.getPrice()
                : variant.getSneaker().getPrice();

        item.setPriceAtTime(currentPrice);
        item.setPriceConfirmed(true);

        cartRedisService.saveRedisCart(username, cart);
        return CartMapper.toCartResponse(cart);
    }
}


