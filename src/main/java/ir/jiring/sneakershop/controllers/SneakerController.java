package ir.jiring.sneakershop.controllers;

import ir.jiring.sneakershop.dto.sneaker.SneakerAddRequest;
import ir.jiring.sneakershop.dto.sneaker.SneakerResponse;
import ir.jiring.sneakershop.dto.sneaker.SneakerUpdateRequest;
import ir.jiring.sneakershop.services.SneakerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/sneakers")
public class SneakerController {

    private final SneakerService sneakerService;

    public SneakerController(SneakerService sneakerService) {
        this.sneakerService = sneakerService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER')")
    public ResponseEntity<SneakerResponse> addSneaker(@Valid @RequestBody SneakerAddRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sneakerService.addSneaker(request));
    }

    @GetMapping("/show")
    public ResponseEntity<Iterable<SneakerResponse>> getAllSneakers() {
        return ResponseEntity.ok(sneakerService.getAllSneakers());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER')")
    public ResponseEntity<SneakerResponse> updateSneaker(@PathVariable UUID id, @Valid @RequestBody SneakerUpdateRequest request) {
        return ResponseEntity.ok(sneakerService.updateSneaker(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER')")
    public ResponseEntity<Void> deleteSneaker(@PathVariable UUID id) {
        sneakerService.deleteSneaker(id);
        return ResponseEntity.noContent().build();
    }

}