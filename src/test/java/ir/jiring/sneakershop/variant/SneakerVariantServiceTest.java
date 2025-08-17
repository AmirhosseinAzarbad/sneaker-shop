package ir.jiring.sneakershop.variant;


import ir.jiring.sneakershop.dto.variant.SneakerVariantAddAndUpdateResponse;
import ir.jiring.sneakershop.dto.variant.SneakerVariantAddRequest;
import ir.jiring.sneakershop.dto.variant.SneakerVariantGetAllResponse;
import ir.jiring.sneakershop.dto.variant.SneakerVariantUpdateRequest;
import ir.jiring.sneakershop.models.Sneaker;
import ir.jiring.sneakershop.models.SneakerVariant;
import ir.jiring.sneakershop.repositories.elasticsearch.SneakerRepositoryElasticsearch;
import ir.jiring.sneakershop.repositories.jpa.SneakerVariantRepository;
import ir.jiring.sneakershop.services.SneakerVariantService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class SneakerVariantServiceTest {

    @Mock
    private SneakerRepositoryElasticsearch sneakerRepositoryElasticSearch;

    @Mock
    private SneakerVariantRepository sneakerVariantRepository;

    @InjectMocks
    private SneakerVariantService service;

    private Sneaker parent;
    private UUID sneakerId;
    private UUID variantId;

    @BeforeEach
    void setup() {
        sneakerId = UUID.randomUUID();
        variantId = UUID.randomUUID();
        parent = new Sneaker();
        parent.setId(sneakerId);
        parent.setName("Test");
        parent.setPrice(new BigDecimal("100"));
    }

    @Nested
    @DisplayName("Add Variant")
    class AddVariant {
        @Test
        @DisplayName("Success -> Add Variant")
        void shouldAddVariant() {
            // given
            SneakerVariantAddRequest req = new SneakerVariantAddRequest();
            req.setSize("42");
            req.setColor("Black");
            req.setStockQuantity(10);
            req.setPrice(new BigDecimal("120"));

            given(sneakerRepositoryElasticSearch.findById(sneakerId)).willReturn(Optional.of(parent));
            given(sneakerVariantRepository.save(any(SneakerVariant.class)))
                    .willAnswer(invocation -> invocation.getArgument(0));

            // when
            SneakerVariantAddAndUpdateResponse resp = service.addVariant(req, sneakerId);

            // then
            assertEquals("42", resp.getSize());
            assertEquals("Black", resp.getColor());
            assertEquals(10, resp.getStockQuantity());
            assertEquals(new BigDecimal("120"), resp.getPrice());
            verify(sneakerVariantRepository).save(any(SneakerVariant.class));
        }

        @Test
        @DisplayName("Sneaker Not Found")
        void shouldThrowWhenSneakerNotFound() {
            given(sneakerRepositoryElasticSearch.findById(sneakerId)).willReturn(Optional.empty());
            SneakerVariantAddRequest req = new SneakerVariantAddRequest();
            assertThrows(EntityNotFoundException.class,
                    () -> service.addVariant(req, sneakerId));
        }
    }

    @Nested
    @DisplayName("Get Variants")
    class GetVariants {
        @Test
        @DisplayName("Success -> Get Variants")
        void shouldReturnVariants() {
            SneakerVariant variant = new SneakerVariant();
            variant.setId(variantId);
            variant.setSneaker(parent);
            given(sneakerRepositoryElasticSearch.findById(sneakerId)).willReturn(Optional.of(parent));
            given(sneakerVariantRepository.findAllBySneakerId(sneakerId))
                    .willReturn(List.of(variant));

            SneakerVariantGetAllResponse resp = service.getVariants(sneakerId);
            assertNotNull(resp);
            assertEquals(parent.getName(), resp.getSneakerName());
            assertEquals(1, resp.getVariants().size());
        }

        @Test
        @DisplayName("No Variants")
        void shouldThrowWhenNoVariants() {
            given(sneakerRepositoryElasticSearch.findById(sneakerId)).willReturn(Optional.of(parent));
            given(sneakerVariantRepository.findAllBySneakerId(sneakerId))
                    .willReturn(Collections.emptyList());

            assertThrows(EntityNotFoundException.class,
                    () -> service.getVariants(sneakerId));
        }
    }

    @Nested
    @DisplayName("Update SneakerVariant")
    class UpdateVariant {
        @Test
        @DisplayName("Success -> Update Variant")
        void shouldUpdateVariant() {
            SneakerVariant existing = new SneakerVariant();
            existing.setId(variantId);
            existing.setSneaker(parent);
            given(sneakerVariantRepository.findById(variantId))
                    .willReturn(Optional.of(existing));
            given(sneakerVariantRepository.save(any(SneakerVariant.class)))
                    .willAnswer(i -> i.getArgument(0));

            SneakerVariantUpdateRequest req = new SneakerVariantUpdateRequest();
            req.setPrice(new BigDecimal("150"));
            req.setStockQuantity(5);

            SneakerVariantAddAndUpdateResponse resp = service.updateSneakerVariant(variantId, req);
            assertEquals(new BigDecimal("150"), resp.getPrice());
            assertEquals(5, resp.getStockQuantity());
        }

        @Test
        @DisplayName("Variant Not Found")
        void shouldThrowWhenVariantNotFound() {
            given(sneakerVariantRepository.findById(variantId)).willReturn(Optional.empty());
            assertThrows(EntityNotFoundException.class,
                    () -> service.updateSneakerVariant(variantId, new SneakerVariantUpdateRequest()));
        }
    }

    @Nested
    @DisplayName("Delete Variant")
    class DeleteVariant {
        @Test
        @DisplayName("Success -> Delete Variant")
        void shouldDeleteVariant() {
            SneakerVariant variant = new SneakerVariant();
            variant.setId(variantId);
            variant.setSneaker(parent);
            parent.setVariants(new ArrayList<>(List.of(variant)));
            given(sneakerVariantRepository.findById(variantId))
                    .willReturn(Optional.of(variant));
            doNothing().when(sneakerVariantRepository).delete(variant);

            service.deleteVariant(variantId);
            verify(sneakerVariantRepository).delete(variant);
            assertTrue(parent.getVariants().isEmpty());
        }

        @Test
        @DisplayName("Variant Not Found")
        void shouldThrowWhenDeleteNotFound() {
            given(sneakerVariantRepository.findById(variantId)).willReturn(Optional.empty());
            assertThrows(EntityNotFoundException.class,
                    () -> service.deleteVariant(variantId));
        }
    }
}

