package controllers;

import models.Sneaker;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import services.SneakerService;

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
        return sneakerService.addSneaker(sneaker);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Sneaker> getSneaker(@PathVariable Long id) {
        return sneakerService.getSneaker(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public Iterable<Sneaker> getAllSneakers() {
        return sneakerService.getAllSneakers();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Sneaker> updateSneaker(@PathVariable Long id, @RequestBody Sneaker sneaker) {
        return sneakerService.updateSneaker(id, sneaker);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSneaker (@PathVariable Long id) {
        return sneakerService.deleteSneaker(id);
    }
}