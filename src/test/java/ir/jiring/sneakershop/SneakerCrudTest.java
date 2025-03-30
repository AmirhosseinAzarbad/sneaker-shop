package ir.jiring.sneakershop;

import controllers.SneakerController;
import models.Sneaker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import services.SneakerService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SneakerCrudTest {

    @InjectMocks
    private SneakerController sneakerController;

    @Mock
    private SneakerService sneakerService;

    private Sneaker sneaker;

    @BeforeEach
    public void setUp() {

        sneaker = new Sneaker();
        sneaker.setId(1L);
        sneaker.setName("Nike Air Max");
        sneaker.setBrand("Nike");
        sneaker.setPrice(120.0);
        sneaker.setSize("42");
        sneaker.setColor("Black");
    }

    @Test
    public void testAddSneaker() {
        when(sneakerService.addSneaker(any(Sneaker.class))).thenReturn(ResponseEntity.ok(sneaker));

        ResponseEntity<Sneaker> response = sneakerController.addSneaker(sneaker);

        assertNotNull(response.getBody(), "Response body should not be null");
        assertEquals(200, response.getStatusCode().value());
        assertEquals(sneaker.getName(), response.getBody().getName());
    }

    @Test
    public void testGetSneaker() {
        when(sneakerService.getSneaker(1L)).thenReturn(Optional.of(sneaker));

        ResponseEntity<Sneaker> response = sneakerController.getSneaker(1L);
        assertNotNull(response.getBody(), "Response body should not be null");
        assertEquals(200, response.getStatusCode().value());
        assertEquals(sneaker.getId(), response.getBody().getId());
    }


    @Test
    public void testGetAllSneakers() {
        when(sneakerService.getAllSneakers()).thenReturn(List.of(sneaker));

        Iterable<Sneaker> sneakers = sneakerController.getAllSneakers();

        assertNotNull(sneakers);
        assertTrue(sneakers.iterator().hasNext());
    }

    @Test
    public void testUpdateSneaker() {
        Sneaker updatedSneaker = new Sneaker();
        updatedSneaker.setName("Nike Air Max Updated");
        updatedSneaker.setBrand("Nike");
        updatedSneaker.setPrice(130.0);
        updatedSneaker.setSize("43");
        updatedSneaker.setColor("White");

        when(sneakerService.updateSneaker(1L, updatedSneaker)).thenReturn(ResponseEntity.ok(updatedSneaker));

        ResponseEntity<Sneaker> response = sneakerController.updateSneaker(1L, updatedSneaker);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody(), "Response body should not be null");
        assertEquals(updatedSneaker.getName(), response.getBody().getName());
    }

    @Test
    public void testDeleteSneaker() {
        SneakerService mockedSneakerService = mock(SneakerService.class);

        when(mockedSneakerService.deleteSneaker(anyLong())).thenReturn(ResponseEntity.noContent().build());

        ResponseEntity<Void> response = mockedSneakerService.deleteSneaker(1L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }


    @Test
    public void testGetSneakerNotFound() {
        when(sneakerService.getSneaker(2L)).thenReturn(Optional.empty());

        ResponseEntity<Sneaker> response = sneakerController.getSneaker(2L);

        assertEquals(404, response.getStatusCode().value());
    }
}