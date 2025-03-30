package services;


import jakarta.persistence.EntityNotFoundException;
import models.Sneaker;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repositories.SneakerRepository;

import java.util.Optional;

@Service
public class SneakerService {

    // Constructor injection
    private final SneakerRepository sneakerRepository;

    public SneakerService(SneakerRepository sneakerRepository) {
        this.sneakerRepository = sneakerRepository;
    }


    // Create Sneaker
    public ResponseEntity<Sneaker> addSneaker(Sneaker sneaker) {
        if (sneaker == null)
            return ResponseEntity.badRequest().build();
        Sneaker validSneaker = sneakerRepository.save(sneaker);
        return ResponseEntity.ok(validSneaker);
    }

    // Read Sneaker
    public Optional<Sneaker> getSneaker(Long id) {
        return sneakerRepository.findById(id);
    }

    public Iterable<Sneaker> getAllSneakers() {
        return sneakerRepository.findAll();
    }

    // Update Sneaker
    @Transactional
    public ResponseEntity<Sneaker> updateSneaker(Long id,Sneaker sneaker) {
        if (sneaker == null)
            return ResponseEntity.badRequest().build();
        Sneaker existingSneaker = sneakerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Sneaker not found"));

            existingSneaker.setName(sneaker.getName());
            existingSneaker.setBrand(sneaker.getBrand());
            existingSneaker.setPrice(sneaker.getPrice());
            existingSneaker.setSize(sneaker.getSize());
            existingSneaker.setColor(sneaker.getColor());
//            sneakerRepository.save(existingSneaker);     will be done automatically by @Transactional
            return ResponseEntity.ok(existingSneaker);

    }

    // Delete Sneaker
    public ResponseEntity<Sneaker> deleteSneaker(Long id) {
        sneakerRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

}
