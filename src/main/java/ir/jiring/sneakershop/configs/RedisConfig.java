package ir.jiring.sneakershop.configs;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import ir.jiring.sneakershop.models.RedisCart;
import ir.jiring.sneakershop.utils.CartKeyProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
//    ocker exec -it redis redis-cli FLUSHALL

@Configuration
public class RedisConfig {

    @Value("${spring.cache.cart.key-prefix}")
    private String keyPrefix;

    @Bean("redisObjectMapper")
    public ObjectMapper redisObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.activateDefaultTyping(
                LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL,
                JsonTypeInfo.As.PROPERTY
        );
        return mapper;
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory();
    }

    @Bean
    public RedisTemplate<String, RedisCart> redisTemplate(
            RedisConnectionFactory factory,
            @Qualifier("redisObjectMapper") ObjectMapper redisObjectMapper) {

        RedisTemplate<String, RedisCart> tpl = new RedisTemplate<>();
        tpl.setConnectionFactory(factory);

        GenericJackson2JsonRedisSerializer serializer =
                new GenericJackson2JsonRedisSerializer(redisObjectMapper);

        tpl.setKeySerializer(new StringRedisSerializer());
        tpl.setValueSerializer(serializer);
        tpl.setHashKeySerializer(new StringRedisSerializer());
        tpl.setHashValueSerializer(serializer);

        tpl.afterPropertiesSet();
        return tpl;
    }

    @Bean
    public RedisTemplate<String, String> customStringRedisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        return template;
    }


    @Bean
    public CartKeyProvider cartKeyProvider() {
        return new CartKeyProvider(keyPrefix);
    }

}

