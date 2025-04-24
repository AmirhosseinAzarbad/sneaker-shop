package ir.jiring.sneakershop.services;


import jakarta.persistence.EntityNotFoundException;
import ir.jiring.sneakershop.models.Sneaker;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import ir.jiring.sneakershop.repositories.SneakerRepository;

@Service
public class SneakerService {

    // Constructor injection
    private final SneakerRepository sneakerRepository;

    public SneakerService(SneakerRepository sneakerRepository) {
        this.sneakerRepository = sneakerRepository;
    }


    // Create Sneaker
    public Sneaker addSneaker(Sneaker sneaker) {
        return sneakerRepository.save(sneaker);
    }

    // Read Sneaker
    public Sneaker getSneaker(Long id) {
        return sneakerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Sneaker not found"));
    }

    public Iterable<Sneaker> getAllSneakers() {
        return sneakerRepository.findAll();
    }

    // Update Sneaker
    public Sneaker updateSneaker(Long id, Sneaker sneaker) {
        Sneaker existingSneaker = sneakerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Sneaker not found"));

        BeanUtils.copyProperties(sneaker, existingSneaker, "id");
//            sneakerRepository.save(existingSneaker);     will be done automatically by @Transactional
        return existingSneaker;

    }

    // Delete Sneaker
    public void deleteSneaker(Long id) {
        sneakerRepository.deleteById(id);
    }

}
