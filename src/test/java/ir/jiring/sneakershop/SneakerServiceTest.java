package ir.jiring.sneakershop;


import ir.jiring.sneakershop.dto.sneaker.SneakerAddRequest;
import ir.jiring.sneakershop.dto.sneaker.SneakerResponse;
import ir.jiring.sneakershop.dto.sneaker.SneakerUpdateRequest;
import ir.jiring.sneakershop.models.Sneaker;
import ir.jiring.sneakershop.models.SneakerVariant;
import ir.jiring.sneakershop.repositories.SneakerRepository;
import ir.jiring.sneakershop.services.SneakerService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SneakerServiceTest {

    @Mock
    private SneakerRepository sneakerRepository;

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
        // add a variant for update test
        SneakerVariant variant = new SneakerVariant();
        variant.setId(UUID.randomUUID());
        variant.setPrice(BigDecimal.valueOf(100));
        sampleSneaker.setVariants(Collections.singletonList(variant));
    }

    @Test
    void addSneaker_shouldSaveAndReturnResponse() {
        SneakerAddRequest request = new SneakerAddRequest();
        request.setName("Air Force 1");
        request.setBrand("Nike");
        request.setPrice(BigDecimal.valueOf(120));

        when(sneakerRepository.save(any(Sneaker.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        SneakerResponse response = sneakerService.addSneaker(request);

        assertThat(response.getName()).isEqualTo("Air Force 1");
        assertThat(response.getBrand()).isEqualTo("Nike");
        assertThat(response.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(120));
        verify(sneakerRepository, times(1)).save(any(Sneaker.class));
    }

    @Test
    void getAllSneakers_shouldReturnListOfResponses() {
        when(sneakerRepository.findAll()).thenReturn(Collections.singletonList(sampleSneaker));

        Iterable<SneakerResponse> responses = sneakerService.getAllSneakers();

        List<SneakerResponse> list = (List<SneakerResponse>) responses;
        assertThat(list).hasSize(1);
        assertThat(list.getFirst().getId()).isEqualTo(sampleId);
        verify(sneakerRepository, times(1)).findAll();
    }

    @Test
    void updateSneaker_existingSneaker_shouldUpdateFieldsAndVariants() {
        SneakerUpdateRequest request = new SneakerUpdateRequest();
        request.setName("Black Cat");
        request.setBrand("Lost and Found");
        request.setPrice(BigDecimal.valueOf(150));

        when(sneakerRepository.findById(sampleId)).thenReturn(Optional.of(sampleSneaker));
        when(sneakerRepository.save(sampleSneaker)).thenReturn(sampleSneaker);

        SneakerResponse response = sneakerService.updateSneaker(sampleId, request);

        assertThat(response.getName()).isEqualTo("Black Cat");
        assertThat(response.getBrand()).isEqualTo("Lost and Found");
        assertThat(response.getPrice()).isEqualByComparingTo(BigDecimal.valueOf(150));
        // confirm variant price updated
        assertThat(sampleSneaker.getVariants().getFirst().getPrice())
                .isEqualByComparingTo(BigDecimal.valueOf(150));
        verify(sneakerRepository).findById(sampleId);
        verify(sneakerRepository).save(sampleSneaker);
    }

    @Test
    void updateSneaker_nonExistingSneaker_shouldThrow() {
        when(sneakerRepository.findById(sampleId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> sneakerService.updateSneaker(sampleId, new SneakerUpdateRequest()));

        verify(sneakerRepository).findById(sampleId);
        verify(sneakerRepository, never()).save(any());
    }

    @Test
    void deleteSneaker_existingSneaker_shouldInvokeDelete() {
        when(sneakerRepository.findById(sampleId)).thenReturn(Optional.of(sampleSneaker));

        sneakerService.deleteSneaker(sampleId);

        verify(sneakerRepository).findById(sampleId);
        verify(sneakerRepository).delete(sampleSneaker);
    }

    @Test
    void deleteSneaker_nonExistingSneaker_shouldThrow() {
        when(sneakerRepository.findById(sampleId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> sneakerService.deleteSneaker(sampleId));

        verify(sneakerRepository).findById(sampleId);
        verify(sneakerRepository, never()).delete(any());
    }
}
