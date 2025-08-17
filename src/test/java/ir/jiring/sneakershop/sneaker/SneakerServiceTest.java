package ir.jiring.sneakershop.sneaker;


import ir.jiring.sneakershop.dto.sneaker.SneakerAddRequest;
import ir.jiring.sneakershop.dto.sneaker.SneakerResponse;
import ir.jiring.sneakershop.dto.sneaker.SneakerUpdateRequest;
import ir.jiring.sneakershop.models.Sneaker;
import ir.jiring.sneakershop.models.SneakerVariant;
import ir.jiring.sneakershop.repositories.elasticsearch.SneakerRepositoryElasticsearch;
import ir.jiring.sneakershop.repositories.jpa.SneakerRepositoryJpa;
import ir.jiring.sneakershop.services.SneakerService;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SneakerService Unit Tests")
class SneakerServiceTest {

    @Mock
    private SneakerRepositoryElasticsearch sneakerRepositoryElasticSearch;

    @Mock
    private SneakerRepositoryJpa sneakerRepositoryJpa;

    @InjectMocks
    private SneakerService sneakerService;

    private Sneaker sampleSneaker;
    private UUID sampleId;

    @BeforeEach
    void setUp() {
        sampleId = UUID.randomUUID();
        sampleSneaker = new Sneaker();
        sampleSneaker.setId(sampleId);
        sampleSneaker.setName("Air Max");
        sampleSneaker.setBrand("Nike");
        sampleSneaker.setPrice(BigDecimal.valueOf(100));

        // prepare one variant for update tests
        SneakerVariant variant = new SneakerVariant();
        variant.setId(UUID.randomUUID());
        variant.setPrice(BigDecimal.valueOf(100));
        sampleSneaker.setVariants(List.of(variant));
    }

    @Nested
    @DisplayName("Add Sneaker")
    class AddSneakerTests {

        @Test
        @DisplayName("should save a new sneaker and return its response")
        void shouldSaveAndReturnResponse() {
            var req = new SneakerAddRequest();
            req.setName("Air Force 1");
            req.setBrand("Nike");
            req.setPrice(BigDecimal.valueOf(120));

            when(sneakerRepositoryElasticSearch.save(any(Sneaker.class)))
                    .thenAnswer(invocation -> invocation.getArgument(0));

            SneakerResponse resp = sneakerService.addSneaker(req);

            assertThat(resp.getName()).isEqualTo("Air Force 1");
            assertThat(resp.getBrand()).isEqualTo("Nike");
            assertThat(resp.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(120));
            verify(sneakerRepositoryElasticSearch, times(1)).save(any(Sneaker.class));
        }
    }

    @Nested
    @DisplayName("Get All Sneakers")
    class GetAllSneakersTests {

        @Test
        @DisplayName("should return all sneakers as responses")
        void shouldReturnListOfResponses() {
            when(sneakerRepositoryJpa.findAll()).thenReturn(List.of(sampleSneaker));

            Iterable<SneakerResponse> responses = sneakerService.getAllSneakers();
            List<SneakerResponse> list = StreamSupport.stream(responses.spliterator(), false)
                    .collect(Collectors.toList());

            assertThat(list).hasSize(1);
            assertThat(list.getFirst().getId()).isEqualTo(sampleId);
            verify(sneakerRepositoryJpa, times(1)).findAll();
        }
    }

    @Nested
    @DisplayName("Update Sneaker")
    class UpdateSneakerTests {

        @Test
        @DisplayName("existing sneaker should be updated and return updated response")
        void existingSneakerShouldUpdateFieldsAndVariants() {
            var req = new SneakerUpdateRequest();
            req.setName("Black Cat");
            req.setBrand("Lost and Found");
            req.setPrice(BigDecimal.valueOf(150));

            when(sneakerRepositoryElasticSearch.findById(sampleId)).thenReturn(Optional.of(sampleSneaker));
            when(sneakerRepositoryElasticSearch.save(sampleSneaker)).thenReturn(sampleSneaker);

            SneakerResponse resp = sneakerService.updateSneaker(sampleId, req);

            assertThat(resp.getName()).isEqualTo("Black Cat");
            assertThat(resp.getBrand()).isEqualTo("Lost and Found");
            assertThat(resp.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(150));
            // variant price should also update
            assertThat(sampleSneaker.getVariants().getFirst().getPrice())
                    .isEqualByComparingTo(BigDecimal.valueOf(150));

            verify(sneakerRepositoryElasticSearch).findById(sampleId);
            verify(sneakerRepositoryElasticSearch).save(sampleSneaker);
        }

        @Test
        @DisplayName("non-existing sneaker should throw EntityNotFoundException")
        void nonExistingSneakerShouldThrow() {
            when(sneakerRepositoryElasticSearch.findById(sampleId)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class,
                    () -> sneakerService.updateSneaker(sampleId, new SneakerUpdateRequest()));

            verify(sneakerRepositoryElasticSearch).findById(sampleId);
            verify(sneakerRepositoryElasticSearch, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Delete Sneaker")
    class DeleteSneakerTests {

        @Test
        @DisplayName("existing sneaker should be deleted")
        void existingSneakerShouldInvokeDelete() {
            when(sneakerRepositoryJpa.findById(sampleId)).thenReturn(Optional.of(sampleSneaker));

            sneakerService.deleteSneaker(sampleId);

            verify(sneakerRepositoryJpa).findById(sampleId);
            verify(sneakerRepositoryJpa).delete(sampleSneaker);
        }

        @Test
        @DisplayName("non-existing sneaker deletion should throw EntityNotFoundException")
        void nonExistingSneakerShouldThrow() {
            when(sneakerRepositoryJpa.findById(sampleId)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class,
                    () -> sneakerService.deleteSneaker(sampleId));

            verify(sneakerRepositoryJpa).findById(sampleId);
            verify(sneakerRepositoryJpa, never()).delete(any());
        }
    }
}

