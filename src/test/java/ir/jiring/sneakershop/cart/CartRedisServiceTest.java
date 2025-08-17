package ir.jiring.sneakershop.cart;

import ir.jiring.sneakershop.mapper.CartMapper;
import ir.jiring.sneakershop.models.*;
import ir.jiring.sneakershop.repositories.jpa.SneakerVariantRepository;
import ir.jiring.sneakershop.repositories.jpa.UserRepository;
import ir.jiring.sneakershop.services.CartRedisService;
import ir.jiring.sneakershop.services.StockManager;
import ir.jiring.sneakershop.utils.CartKeyProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class CartRedisServiceTest {

    @Mock RedisTemplate<String, RedisCart> redisTemplate;
    @Mock RedisTemplate<String, String> stringRedisTemplate;
    @Mock CartKeyProvider keyProvider;
    @Mock StockManager stockManager;
    @Mock UserRepository userRepo;
    @Mock SneakerVariantRepository varRepo;

    private CartRedisService service;

    private static final String USER       = "alice";
    private static final String DATA_KEY   = "cart:data:alice";
    private static final String EXP_KEY    = "cart:exp:alice";
    private static final UUID   VARIANT_ID = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        service = new CartRedisService(
                redisTemplate,
                stringRedisTemplate,
                keyProvider,
                stockManager,
                userRepo,
                varRepo,
                Duration.ofMinutes(5)
        );
    }

    @Test
    @DisplayName("saveRedisCart should store data and expiration keys")
    void saveRedisCart_storesDataAndExp() {
        given(keyProvider.dataKeyFor(USER)).willReturn(DATA_KEY);
        given(keyProvider.expKeyFor(USER)).willReturn(EXP_KEY);

        @SuppressWarnings("unchecked")
        ValueOperations<String, RedisCart> dataOps = mock(ValueOperations.class);
        @SuppressWarnings("unchecked")
        ValueOperations<String, String>    expOps  = mock(ValueOperations.class);
        given(redisTemplate.opsForValue()).willReturn(dataOps);
        given(stringRedisTemplate.opsForValue()).willReturn(expOps);

        Cart cart = new Cart();
        User user = new User(); user.setUserId(1L); user.setUsername(USER);
        cart.setUser(user);
        cart.setItems(Collections.emptyList());

        service.saveRedisCart(USER, cart);

        then(dataOps).should().set(eq(DATA_KEY), any(RedisCart.class));
        then(expOps). should().set(eq(EXP_KEY), eq(""), any(Duration.class));
    }

    @Test
    @DisplayName("getRedisCart returns empty when no entry")
    void getRedisCart_empty() {
        given(keyProvider.dataKeyFor(USER)).willReturn(DATA_KEY);

        @SuppressWarnings("unchecked")
        ValueOperations<String, RedisCart> dataOps = mock(ValueOperations.class);
        given(redisTemplate.opsForValue()).willReturn(dataOps);
        given(dataOps.get(DATA_KEY)).willReturn(null);

        Optional<Cart> result = service.getRedisCart(USER);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("getRedisCart returns cart when entry exists")
    void getRedisCart_nonEmpty() {
        given(keyProvider.dataKeyFor(USER)).willReturn(DATA_KEY);

        @SuppressWarnings("unchecked")
        ValueOperations<String, RedisCart> dataOps = mock(ValueOperations.class);
        given(redisTemplate.opsForValue()).willReturn(dataOps);

        Sneaker sneaker = new Sneaker(); sneaker.setName("TestSneaker");
        SneakerVariant variant = new SneakerVariant();
        variant.setId(VARIANT_ID);
        variant.setSneaker(sneaker);
        variant.setPrice(BigDecimal.valueOf(20));

        CartItem item = new CartItem();
        item.setId(UUID.randomUUID());
        item.setVariant(variant);
        item.setQuantity(2);
        item.setPriceAtTime(BigDecimal.valueOf(20));
        item.setPriceConfirmed(true);

        Cart original = new Cart();
        User user = new User(); user.setUserId(1L); user.setUsername(USER);
        original.setUser(user);
        original.setItems(Collections.singletonList(item));

        RedisCart rc = CartMapper.toRedisCart(original);
        given(dataOps.get(DATA_KEY)).willReturn(rc);

        given(userRepo.findById(1L)).willReturn(Optional.of(user));
        given(varRepo.findById(VARIANT_ID)).willReturn(Optional.of(variant));

        Optional<Cart> result = service.getRedisCart(USER);
        assertTrue(result.isPresent());

        Cart restored = result.get();
        assertEquals(USER, restored.getUser().getUsername());
        assertEquals(1, restored.getItems().size());

        CartItem restoredItem = restored.getItems().getFirst();
        assertEquals(VARIANT_ID, restoredItem.getVariant().getId());
        assertEquals(2, restoredItem.getQuantity());
    }

    @Test
    @DisplayName("deleteAfterCheckout removes both keys")
    void deleteAfterCheckout_deletesKeys() {
        given(keyProvider.dataKeyFor(USER)).willReturn(DATA_KEY);
        given(keyProvider.expKeyFor(USER)).willReturn(EXP_KEY);

        service.deleteAfterCheckout(USER);

        then(redisTemplate).should().delete(DATA_KEY);
        then(redisTemplate).should().delete(EXP_KEY);
    }

    @Nested
    @DisplayName("forceReleaseCart behavior")
    class ForceReleaseTests {
        @Test
        @DisplayName("does nothing and deletes key when no cart exists")
        void noCart_deletesOnlyDataKey() {
            given(keyProvider.dataKeyFor(USER)).willReturn(DATA_KEY);

            @SuppressWarnings("unchecked")
            ValueOperations<String, RedisCart> dataOps = mock(ValueOperations.class);
            given(redisTemplate.opsForValue()).willReturn(dataOps);
            given(dataOps.get(DATA_KEY)).willReturn(null);

            service.forceReleaseCart(USER);

            then(stockManager).should(never()).releaseCart(any());
            then(redisTemplate).should().delete(DATA_KEY);
        }

        @Test
        @DisplayName("releases stock and deletes key when cart exists")
        void withCart_releasesAndDeletes() {
            given(keyProvider.dataKeyFor(USER)).willReturn(DATA_KEY);

            @SuppressWarnings("unchecked")
            ValueOperations<String, RedisCart> dataOps = mock(ValueOperations.class);
            given(redisTemplate.opsForValue()).willReturn(dataOps);

            Sneaker sneaker = new Sneaker(); sneaker.setName("Shoe");
            SneakerVariant variant = new SneakerVariant();
            variant.setId(VARIANT_ID);
            variant.setSneaker(sneaker);
            variant.setPrice(BigDecimal.valueOf(30));

            CartItem item = new CartItem();
            item.setVariant(variant);
            item.setQuantity(1);
            item.setPriceAtTime(BigDecimal.valueOf(30));

            Cart cart = new Cart();
            User user = new User(); user.setUserId(1L); user.setUsername(USER);
            cart.setUser(user);
            cart.setItems(Collections.singletonList(item));

            RedisCart rc = CartMapper.toRedisCart(cart);
            given(dataOps.get(DATA_KEY)).willReturn(rc);

            given(userRepo.findById(1L)).willReturn(Optional.of(user));
            given(varRepo.findById(VARIANT_ID)).willReturn(Optional.of(variant));

            service.forceReleaseCart(USER);

            then(stockManager).should().releaseCart(argThat(c ->
                    USER.equals(c.getUser().getUsername()) &&
                            c.getItems().size() == 1
            ));
            then(redisTemplate).should().delete(DATA_KEY);
        }
    }
}
