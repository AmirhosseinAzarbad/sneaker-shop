package ir.jiring.sneakershop.exceptions;

import ir.jiring.sneakershop.models.CartItem;
import lombok.Getter;

import java.util.List;

@Getter
public class PriceMismatchException extends RuntimeException {
    private final List<CartItem> mismatchedItems;

    public PriceMismatchException(String message, List<CartItem> items) {
        super(message);
        this.mismatchedItems = items;
    }

}
