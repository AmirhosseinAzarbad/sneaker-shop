package ir.jiring.sneakershop.services;


import ir.jiring.sneakershop.dto.sneaker.SneakerAddRequest;
import ir.jiring.sneakershop.dto.sneaker.SneakerResponse;
import ir.jiring.sneakershop.dto.sneaker.SneakerUpdateRequest;
import ir.jiring.sneakershop.mapper.SneakerMapper;
import ir.jiring.sneakershop.models.Sneaker;
import ir.jiring.sneakershop.models.SneakerVariant;
import ir.jiring.sneakershop.repositories.SneakerRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SneakerService {

    private final SneakerRepository sneakerRepository;

    public SneakerService(SneakerRepository sneakerRepository) {
        this.sneakerRepository = sneakerRepository;
    }

    // Create Sneaker
    @Transactional
    public SneakerResponse addSneaker(SneakerAddRequest request) {
        Sneaker sneaker = new Sneaker();
        sneaker.setName(request.getName());
        sneaker.setBrand(request.getBrand());
        sneaker.setPrice(request.getPrice());
        sneakerRepository.save(sneaker);
        return SneakerMapper.toSneakerResponseDTO(sneaker);
    }


    public Iterable<SneakerResponse> getAllSneakers() {
        List<Sneaker> sneakers = sneakerRepository.findAll();

        return sneakers.stream()
                .map(SneakerMapper::toSneakerResponseDTO)
                .collect(Collectors.toList());
    }

    // Update Sneaker
    @Transactional
    public SneakerResponse updateSneaker(UUID id, SneakerUpdateRequest request) {
        Sneaker sneaker = sneakerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Sneaker not found"));
        if (request.getBrand() != null) {
            sneaker.setBrand(request.getBrand());
        }
        if (request.getName() != null) {
            sneaker.setName(request.getName());
        }
        if (request.getPrice() != null) {
            BigDecimal newPrice = request.getPrice();
            sneaker.setPrice(newPrice);

            for (SneakerVariant variant : sneaker.getVariants()) {
                variant.setPrice(newPrice);
            }
        }
        sneakerRepository.save(sneaker);
        return SneakerMapper.toSneakerResponseDTO(sneaker);
    }

    // Delete Sneaker
    @Transactional
    public void deleteSneaker(UUID id) {
        Sneaker sneaker = sneakerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Sneaker not found"));
        sneakerRepository.delete(sneaker);
    }

}
