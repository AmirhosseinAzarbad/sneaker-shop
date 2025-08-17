package ir.jiring.sneakershop.services;

import ir.jiring.sneakershop.exceptions.InsufficientStockException;
import ir.jiring.sneakershop.models.Cart;
import ir.jiring.sneakershop.models.SneakerVariant;
import ir.jiring.sneakershop.repositories.jpa.SneakerVariantRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class StockManager {
    private final SneakerVariantRepository variantRepo;

    public StockManager(SneakerVariantRepository variantRepo) {
        this.variantRepo = variantRepo;
    }

    @Transactional
    public void reserve(UUID variantId, Integer quantity) {
        SneakerVariant variant = variantRepo.findByIdForUpdate(variantId)
                .orElseThrow(() -> new EntityNotFoundException("Variant not found"));
        if (variant.getStockQuantity() < quantity)
            throw new InsufficientStockException("Not enough stock");
        variant.setStockQuantity(variant.getStockQuantity() - quantity);
    }

    @Transactional
    public void release(UUID variantId, Integer quantity) {
        SneakerVariant variant = variantRepo.findByIdForUpdate(variantId)
                .orElseThrow(() -> new EntityNotFoundException("Variant not found"));

        variant.setStockQuantity(variant.getStockQuantity() + quantity);
    }
    @Transactional
    public void releaseCart(Cart cart) {
        cart.getItems().forEach(it -> {
            System.out.println("Releasing stock for variant: " + it.getVariant().getId() + " qty: " + it.getQuantity());
            release(it.getVariant().getId(), it.getQuantity());
        });
    }
}
