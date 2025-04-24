package ir.jiring.sneakershop.controllers;

import jakarta.persistence.EntityNotFoundException;
import ir.jiring.sneakershop.models.Sneaker;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ir.jiring.sneakershop.services.SneakerService;

@RestController
@RequestMapping("/api/sneakers")
public class SneakerController {

    // Constructor injection
    private final SneakerService sneakerService;

    public SneakerController(SneakerService sneakerService) {
        this.sneakerService = sneakerService;
    }

    @PostMapping
    public ResponseEntity<Sneaker> addSneaker(@RequestBody Sneaker sneaker) {
        Sneaker savedSneaker = sneakerService.addSneaker(sneaker);
        return ResponseEntity.ok(savedSneaker);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Sneaker> getSneaker(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(sneakerService.getSneaker(id));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public Iterable<Sneaker> getAllSneakers() {
        return sneakerService.getAllSneakers();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Sneaker> updateSneaker(@PathVariable Long id, @RequestBody Sneaker sneaker) {
        try {
            Sneaker updatedSneaker = sneakerService.updateSneaker(id, sneaker);
            return ResponseEntity.ok(updatedSneaker);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSneaker(@PathVariable Long id) {
        sneakerService.deleteSneaker(id);
        return ResponseEntity.noContent().build();
    }
}