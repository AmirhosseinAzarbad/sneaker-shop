package ir.jiring.sneakershop.utils;

import org.springframework.beans.factory.annotation.Value;

public class CartKeyProvider {

    private final String prefix;


    public CartKeyProvider(@Value("${spring.cache.cart.key-prefix}") String prefix) {
        this.prefix = prefix;
    }

    public String dataKeyFor(String username) {
        return prefix + "data:" + username;
    }

    public String expKeyFor(String username) {
        return prefix + username;
    }

    public String expPrefix() {
        return prefix;
    }


    public String usernameFromExpKey(String key) {
        if (!key.startsWith(prefix)) {
            throw new IllegalArgumentException("Key does not start with expected prefix");
        }
        return key.substring(prefix.length());
    }
}