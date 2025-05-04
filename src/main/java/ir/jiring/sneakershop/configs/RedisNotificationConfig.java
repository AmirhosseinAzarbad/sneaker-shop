package ir.jiring.sneakershop.configs;

import ir.jiring.sneakershop.services.CartRedisService;
import ir.jiring.sneakershop.utils.CartKeyProvider;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

@Configuration
public class RedisNotificationConfig {

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(
            RedisConnectionFactory connectionFactory,
            MessageListener redisKeyExpiredListener) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);

        container.addMessageListener(
                redisKeyExpiredListener,
                new PatternTopic("__keyevent@*__:expired")
        );
        return container;
    }

    @Bean
    public MessageListener redisKeyExpiredListener(CartRedisService cartRedis,
                                                   CartKeyProvider keyProvider) {
        return (message, pattern) -> {
            String expiredKey = message.toString();
            if (expiredKey.startsWith(keyProvider.expPrefix())) {
                String username = keyProvider.usernameFromExpKey(expiredKey);
                cartRedis.forceReleaseCart(username);
            }
        };
    }

    @Bean
    public ApplicationRunner enableRedisNotifications(RedisConnectionFactory factory) {
        return args -> {
            try (RedisConnection connection = factory.getConnection()) {
                connection.serverCommands()
                        .setConfig("notify-keyspace-events", "Ex");
            }
        };
    }
}