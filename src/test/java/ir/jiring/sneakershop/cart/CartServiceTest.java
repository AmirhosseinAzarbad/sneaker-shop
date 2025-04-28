package ir.jiring.sneakershop.cart;

import ir.jiring.sneakershop.dto.cart.AddToCartRequest;
import ir.jiring.sneakershop.dto.cart.CartResponse;
import ir.jiring.sneakershop.dto.cart.CartUpdateRequest;
import ir.jiring.sneakershop.dto.cart.CheckoutRequest;
import ir.jiring.sneakershop.enums.CartStatus;
import ir.jiring.sneakershop.exceptions.EmptyCartException;
import ir.jiring.sneakershop.exceptions.PriceMismatchException;
import ir.jiring.sneakershop.models.*;
import ir.jiring.sneakershop.repositories.CartItemRepository;
import ir.jiring.sneakershop.repositories.SneakerVariantRepository;
import ir.jiring.sneakershop.repositories.UserRepository;
import ir.jiring.sneakershop.services.CartRedisService;
import ir.jiring.sneakershop.services.CartService;
import ir.jiring.sneakershop.services.StockManager;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock CartRedisService cartRedisService;
    @Mock UserRepository userRepo;
    @Mock SneakerVariantRepository variantRepo;
    @Mock CartItemRepository cartItemRepo;
    @Mock StockManager stockManager;

    @InjectMocks CartService cartService;

    private final String USER = "alice";
    private final UUID VARIANT_ID = UUID.randomUUID();

    @Nested
    @DisplayName("checkout validations")
    class CheckoutTests {
        @Test @DisplayName("throws when cart empty")
        void emptyCart_throws() {
            Cart empty = new Cart();
            empty.setItems(Collections.emptyList());
            given(cartRedisService.getRedisCart(USER)).willReturn(Optional.of(empty));

            assertThrows(EmptyCartException.class,
                    () -> cartService.checkout(USER, null));
        }

        @Test @DisplayName("throws when price mismatched")
        void mismatchedPrice_throws() {
            Sneaker sneaker = new Sneaker();
            sneaker.setName("TestSneaker");

            SneakerVariant variant = new SneakerVariant();
            variant.setId(VARIANT_ID);
            variant.setSneaker(sneaker);
            variant.setPrice(BigDecimal.valueOf(50));

            CartItem item = new CartItem();
            item.setVariant(variant);
            item.setQuantity(1);
            item.setPriceAtTime(BigDecimal.valueOf(40)); // old price

            Cart cart = new Cart();
            cart.setItems(Collections.singletonList(item));
            given(cartRedisService.getRedisCart(USER)).willReturn(Optional.of(cart));

            assertThrows(PriceMismatchException.class,
                    () -> cartService.checkout(USER, mock(CheckoutRequest.class)));
        }
    }

    @Nested
    @DisplayName("addToCart behavior")
    class AddTests {
        @Test @DisplayName("creates new item when none present")
        void addNewItem_success() {
            AddToCartRequest req = new AddToCartRequest();
            req.setSneakerId(VARIANT_ID);
            req.setSelectedSize("M");
            req.setSelectedColor("Black");
            req.setQuantity(2);

            Sneaker sneaker = new Sneaker();
            sneaker.setName("AirTest");
            SneakerVariant variant = new SneakerVariant();
            variant.setId(VARIANT_ID);
            variant.setSneaker(sneaker);
            variant.setPrice(BigDecimal.valueOf(99));
            given(variantRepo.findBySneakerIdAndSizeAndColor(any(), any(), any()))
                    .willReturn(Optional.of(variant));

            Cart cart = new Cart();
            given(cartRedisService.getRedisCart(USER)).willReturn(Optional.of(cart));

            CartResponse resp = cartService.addToCart(USER, req);

            assertEquals(1, resp.getItems().size());
            assertEquals(2, resp.getItems().getFirst().getQuantity());
            then(stockManager).should().reserve(VARIANT_ID, 2);
        }

        @Test @DisplayName("exception when variant not found")
        void missingVariant_throws() {
            given(userRepo.findByUsername(anyString())).willReturn(Optional.of(new User()));
            given(variantRepo.findBySneakerIdAndSizeAndColor(any(), any(), any()))
                    .willReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class,
                    () -> cartService.addToCart(USER, new AddToCartRequest()));
        }
    }

    @Nested
    @DisplayName("updateCartItem behavior")
    class UpdateTests {
        CartItem existing;
        Cart cart;

        @BeforeEach
        void setup() {
            // prepare a Sneaker + Variant
            Sneaker sneaker = new Sneaker();
            sneaker.setName("TestSneaker");

            SneakerVariant variant = new SneakerVariant();
            variant.setId(VARIANT_ID);
            variant.setSneaker(sneaker);
            variant.setPrice(BigDecimal.valueOf(10));    // unit price

            // existing CartItem must have variant and priceAtTime
            existing = new CartItem();
            existing.setId(UUID.randomUUID());
            existing.setQuantity(3);
            existing.setVariant(variant);
            existing.setPriceAtTime(BigDecimal.valueOf(10));

            // active Cart with that item and user
            cart = new Cart();
            User user = new User();
            user.setUsername(USER);
            cart.setUser(user);
            cart.setStatus(CartStatus.ACTIVE);
            cart.getItems().add(existing);

            given(cartRedisService.getRedisCart(USER)).willReturn(Optional.of(cart));
        }

        @Test @DisplayName("decrease quantity releases stock")
        void decrease_releases() {
            CartUpdateRequest req = new CartUpdateRequest();
            req.setQuantity(1);

            CartResponse resp = cartService.updateCartItem(USER, existing.getId(), req);

            then(stockManager).should().release(VARIANT_ID, 2);
            assertEquals(1, resp.getItems().getFirst().getQuantity());
        }

        @Test @DisplayName("quantity <1 removes item")
        void zero_removes() {
            CartUpdateRequest req = new CartUpdateRequest();
            req.setQuantity(0);

            CartResponse resp = cartService.updateCartItem(USER, existing.getId(), req);

            assertTrue(resp.getItems().isEmpty());
        }
    }
}
