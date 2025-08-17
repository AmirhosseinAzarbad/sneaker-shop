package ir.jiring.sneakershop.services;


import ir.jiring.sneakershop.dto.sneaker.SneakerAddRequest;
import ir.jiring.sneakershop.dto.sneaker.SneakerResponse;
import ir.jiring.sneakershop.dto.sneaker.SneakerUpdateRequest;
import ir.jiring.sneakershop.mapper.SneakerMapper;
import ir.jiring.sneakershop.models.Sneaker;
import ir.jiring.sneakershop.models.SneakerVariant;
import ir.jiring.sneakershop.repositories.elasticsearch.SneakerRepositoryElasticsearch;
import ir.jiring.sneakershop.repositories.jpa.SneakerRepositoryJpa;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SneakerService {

    @Qualifier("sneakerRepositoryElasticsearch")
    private final SneakerRepositoryElasticsearch sneakerRepositoryElasticSearch;

    @Qualifier("sneakerRepositoryJpa")
    private final SneakerRepositoryJpa sneakerRepositoryJpa;

    ElasticsearchTemplate elasticsearchTemplate;

    public SneakerService(SneakerRepositoryElasticsearch sneakerRepositoryElasticSearch,
                          SneakerRepositoryJpa sneakerRepositoryJpa,
                          ElasticsearchTemplate elasticsearchTemplate) {
        this.sneakerRepositoryElasticSearch = sneakerRepositoryElasticSearch;
        this.sneakerRepositoryJpa = sneakerRepositoryJpa;
        this.elasticsearchTemplate = elasticsearchTemplate;
    }

    // Create Sneaker
    @Transactional
    public SneakerResponse addSneaker(SneakerAddRequest request) {
        Sneaker sneaker = new Sneaker();
        sneaker.setName(request.getName());
        sneaker.setBrand(request.getBrand());
        sneaker.setPrice(request.getPrice());
        sneakerRepositoryJpa.save(sneaker);
        sneakerRepositoryElasticSearch.save(sneaker);
        return SneakerMapper.toSneakerResponseDTO(sneaker);
    }



    public Iterable<SneakerResponse> getAllSneakers() {
        List<Sneaker> sneakers = sneakerRepositoryJpa.findAll();

        return sneakers.stream()
                .map(SneakerMapper::toSneakerResponseDTO)
                .collect(Collectors.toList());
    }

    // Update Sneaker
    @Transactional
    public SneakerResponse updateSneaker(UUID id, SneakerUpdateRequest request) {
        Sneaker sneaker = sneakerRepositoryElasticSearch.findById(id)
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
        sneakerRepositoryJpa.save(sneaker);
        sneakerRepositoryElasticSearch.save(sneaker);
        return SneakerMapper.toSneakerResponseDTO(sneaker);
    }

    // Delete Sneaker
    @Transactional
    public void deleteSneaker(UUID id) {
        Sneaker sneaker = sneakerRepositoryJpa.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Sneaker not found"));
        sneakerRepositoryJpa.delete(sneaker);
        sneakerRepositoryElasticSearch.delete(sneaker);
    }
}
