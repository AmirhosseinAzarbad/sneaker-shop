package ir.jiring.sneakershop.services;

import ir.jiring.sneakershop.mapper.CartMapper;
import ir.jiring.sneakershop.models.Cart;
import ir.jiring.sneakershop.models.RedisCart;
import ir.jiring.sneakershop.repositories.SneakerVariantRepository;
import ir.jiring.sneakershop.repositories.UserRepository;
import ir.jiring.sneakershop.utils.CartKeyProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
public class CartRedisService {
    private final RedisTemplate<String, RedisCart> redisTemplate;
    private final CartKeyProvider keyProvider;
    private final StockManager stockManager;
    private final UserRepository userRepo;
    private final SneakerVariantRepository varRepo;
    private final Duration ttl;
    private final RedisTemplate<String, String> stringRedisTemplate;

    public CartRedisService(
            RedisTemplate<String, RedisCart> redisTemplate,
            RedisTemplate<String, String> stringRedisTemplate,
            CartKeyProvider keyProvider,
            StockManager stockManager,
            UserRepository userRepo,
            SneakerVariantRepository varRepo,
            @Value("${spring.cache.cart.ttl}") Duration ttl
    ) {
        this.keyProvider = keyProvider;
        this.stockManager = stockManager;
        this.userRepo = userRepo;
        this.varRepo = varRepo;
        this.redisTemplate = redisTemplate;
        this.stringRedisTemplate = stringRedisTemplate;
        this.ttl = ttl;

    }


    public void saveRedisCart(String username, Cart cart) {
        String dataKey = keyProvider.dataKeyFor(username);
        String expKey = keyProvider.expKeyFor(username);
        RedisCart rc = CartMapper.toRedisCart(cart);
        redisTemplate.opsForValue().set(dataKey, rc);
        stringRedisTemplate.opsForValue().set(expKey,"",ttl);
    }

    public Optional<Cart> getRedisCart(String username) {
        String dataKey = keyProvider.dataKeyFor(username);
        RedisCart rc = redisTemplate.opsForValue().get(dataKey);
        return Optional.ofNullable(rc)
                .map(r -> CartMapper.fromRedisCart(r, userRepo, varRepo));
    }

    public void deleteAfterCheckout(String username) {
        redisTemplate.delete(keyProvider.dataKeyFor(username));
        redisTemplate.delete(keyProvider.expKeyFor(username));
    }

    public void forceReleaseCart(String username) {
        String dataKey = keyProvider.dataKeyFor(username);
        RedisCart rc = redisTemplate.opsForValue().get(dataKey);
        if (rc != null) {
            Cart cart = CartMapper.fromRedisCart(rc, userRepo, varRepo);
            stockManager.releaseCart(cart);
        }
        redisTemplate.delete(dataKey);
    }
}