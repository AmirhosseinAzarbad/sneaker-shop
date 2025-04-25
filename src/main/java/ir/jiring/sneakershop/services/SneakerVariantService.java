package ir.jiring.sneakershop.services;

import ir.jiring.sneakershop.dto.variant.SneakerVariantAddAndUpdateResponse;
import ir.jiring.sneakershop.dto.variant.SneakerVariantAddRequest;
import ir.jiring.sneakershop.dto.variant.SneakerVariantGetAllResponse;
import ir.jiring.sneakershop.dto.variant.SneakerVariantUpdateRequest;
import ir.jiring.sneakershop.mapper.SneakerVariantMapper;
import ir.jiring.sneakershop.models.Sneaker;
import ir.jiring.sneakershop.models.SneakerVariant;
import ir.jiring.sneakershop.repositories.SneakerRepository;
import ir.jiring.sneakershop.repositories.SneakerVariantRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class SneakerVariantService {

    private final SneakerRepository sneakerRepository;
    private final SneakerVariantRepository sneakerVariantRepository;

    public SneakerVariantService(SneakerVariantRepository sneakerVariantRepository, SneakerRepository sneakerRepository) {
        this.sneakerVariantRepository = sneakerVariantRepository;
        this.sneakerRepository = sneakerRepository;
    }


    public SneakerVariantAddAndUpdateResponse addVariant(SneakerVariantAddRequest request, UUID SneakerId) {
        Sneaker sneaker = sneakerRepository.findById(SneakerId)
                .orElseThrow(() -> new EntityNotFoundException("Sneaker not found "));

        SneakerVariant variant = new SneakerVariant();
        variant.setSize(request.getSize());
        variant.setColor(request.getColor());
        variant.setStockQuantity(request.getStockQuantity());
        variant.setPrice(request.getPrice() != null ? request.getPrice() : sneaker.getPrice());
        variant.setSneaker(sneaker);
        sneakerVariantRepository.save(variant);

        return SneakerVariantMapper.toAddAndUpdateVariantResponseDTO(variant,sneaker);
    }

    public SneakerVariantGetAllResponse getVariants(UUID sneakerId) {

        Sneaker sneaker = sneakerRepository.findById(sneakerId)
                .orElseThrow(()-> new EntityNotFoundException("Sneaker not found"));


        List<SneakerVariant> variants = sneakerVariantRepository.findAllBySneakerId(sneakerId);
        if (variants.isEmpty()) {
            SneakerVariantMapper.toGetAllVariantResponseDTO(sneaker,variants);
            throw new EntityNotFoundException("No variant found");
        }
        return SneakerVariantMapper.toGetAllVariantResponseDTO(sneaker,variants);
    }


    public SneakerVariantAddAndUpdateResponse updateSneakerVariant(UUID id, SneakerVariantUpdateRequest request) {
        SneakerVariant variant = sneakerVariantRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("SneakerVariant not found"));

        Sneaker sneaker = variant.getSneaker();

        if (request.getColor() != null) {
            variant.setColor(request.getColor());
        }

        if (request.getSize() != null) {
            variant.setSize(request.getSize());
        }

        if (request.getPrice() != null) {
            variant.setPrice(request.getPrice());
        }

        if (request.getStockQuantity() != null) {
            variant.setStockQuantity(request.getStockQuantity());
        }


        sneakerVariantRepository.save(variant);

        return SneakerVariantMapper.toAddAndUpdateVariantResponseDTO(variant,sneaker);
    }

    public void deleteVariant(UUID id) {
        SneakerVariant variant = sneakerVariantRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("variant not found"));
        variant.getSneaker().getVariants().remove(variant);
        sneakerVariantRepository.delete(variant);
    }
}
