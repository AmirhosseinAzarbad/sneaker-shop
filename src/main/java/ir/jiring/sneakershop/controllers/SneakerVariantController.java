package ir.jiring.sneakershop.controllers;

import ir.jiring.sneakershop.dto.variant.SneakerVariantAddAndUpdateResponse;
import ir.jiring.sneakershop.dto.variant.SneakerVariantAddRequest;
import ir.jiring.sneakershop.dto.variant.SneakerVariantGetAllResponse;
import ir.jiring.sneakershop.dto.variant.SneakerVariantUpdateRequest;
import ir.jiring.sneakershop.services.SneakerVariantService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/sneaker-variants")

public class SneakerVariantController {

    private final SneakerVariantService sneakerVariantService;

    public SneakerVariantController(SneakerVariantService sneakerVariantService) {
        this.sneakerVariantService = sneakerVariantService;
    }
    @PostMapping("/add/{id}")
    @PreAuthorize("hasRole('OWNER') or hasRole('ADMIN') ")
    public ResponseEntity<SneakerVariantAddAndUpdateResponse> addSneaker(@Valid @RequestBody SneakerVariantAddRequest request, @PathVariable UUID id) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sneakerVariantService.addVariant(request,id));
    }

    @GetMapping("/show/{id}")
    public ResponseEntity<SneakerVariantGetAllResponse> getAllSneakerVariants(@PathVariable UUID id) {
        return ResponseEntity.ok(sneakerVariantService.getVariants(id));
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER')")
    public ResponseEntity<SneakerVariantAddAndUpdateResponse> updateSneakerVariant(
            @PathVariable UUID id,
            @Valid@RequestBody SneakerVariantUpdateRequest request) {

        SneakerVariantAddAndUpdateResponse response = sneakerVariantService.updateSneakerVariant(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('OWNER')")
    public ResponseEntity<Void> deleteVariant(@PathVariable UUID id) {
        sneakerVariantService.deleteVariant(id);
        return ResponseEntity.noContent().build();
    }
}
