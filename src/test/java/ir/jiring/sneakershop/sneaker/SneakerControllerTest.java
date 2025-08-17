package ir.jiring.sneakershop.sneaker;


import com.fasterxml.jackson.databind.ObjectMapper;
import ir.jiring.sneakershop.dto.sneaker.SneakerAddRequest;
import ir.jiring.sneakershop.dto.sneaker.SneakerResponse;
import ir.jiring.sneakershop.dto.sneaker.SneakerUpdateRequest;
import ir.jiring.sneakershop.services.SneakerService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class SneakerControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    @Qualifier("objectMapper")
    private ObjectMapper json;

    @MockitoBean
    private SneakerService service;

/*these fields might will be used in future
    // Required for JwtAuthenticationFilter constructor
    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private UserRepository userRepository;*/

    private final UUID SAMPLE_ID = UUID.fromString("123e4567-e89b-12d3-a456-556642440000");

    @Nested
    @DisplayName("POST /api/sneakers")
    class AddSneaker {

        @Test
        @DisplayName("201 CREATED for ADMIN role with valid request")
        @WithMockUser(roles = "ADMIN")
        void createdForAdmin() throws Exception {
            SneakerAddRequest req = new SneakerAddRequest();
            req.setName("Air Force 1");
            req.setBrand("Nike");
            req.setPrice(BigDecimal.valueOf(120));
            SneakerResponse resp = new SneakerResponse();
            resp.setId(SAMPLE_ID);
            resp.setName("Air Force 1");
            resp.setBrand("Nike");
            resp.setPrice(BigDecimal.valueOf(120));
            given(service.addSneaker(any(SneakerAddRequest.class))).willReturn(resp);

            mvc.perform(post("/api/sneakers")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json.writeValueAsString(req)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(SAMPLE_ID.toString()));
        }

        @Test
        @DisplayName("401 UNAUTHORIZED when not authenticated")
        void unauthorizedWhenNoAuth() throws Exception {
            SneakerAddRequest req = new SneakerAddRequest();
            req.setName("X");
            req.setBrand("Y");
            req.setPrice(BigDecimal.ONE);

            mvc.perform(post("/api/sneakers")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json.writeValueAsString(req)))
                    .andExpect(status().isUnauthorized());
        }

        @Test
        @DisplayName("400 BAD_REQUEST for invalid payload")
        @WithMockUser(roles = "ADMIN")
        void badRequestForInvalidBody() throws Exception {
            SneakerAddRequest req = new SneakerAddRequest();
            req.setName("");
            req.setBrand("");
            req.setPrice(BigDecimal.ZERO);

            mvc.perform(post("/api/sneakers")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json.writeValueAsString(req)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Test
    @DisplayName("GET /api/sneakers/show → 200 OK public")
    void listPublic() throws Exception {
        SneakerResponse resp = new SneakerResponse();
        resp.setId(SAMPLE_ID);
        resp.setName("Air Max");
        resp.setBrand("Nike");
        resp.setPrice(BigDecimal.valueOf(100));
        given(service.getAllSneakers()).willReturn(Collections.singletonList(resp));

        mvc.perform(get("/api/sneakers/show"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(SAMPLE_ID.toString()));
    }

    @Nested
    @DisplayName("PUT /api/sneakers/{id}")
    class UpdateSneaker {

        @Test
        @DisplayName("200 OK for OWNER role with valid request")
        @WithMockUser(roles = "OWNER")
        void okForOwner() throws Exception {
            SneakerUpdateRequest req = new SneakerUpdateRequest();
            req.setName("Air Zoom");
            req.setBrand("Adidas");
            req.setPrice(BigDecimal.valueOf(150));
            SneakerResponse resp = new SneakerResponse();
            resp.setId(SAMPLE_ID);
            resp.setName("Air Zoom");
            resp.setBrand("Adidas");
            resp.setPrice(BigDecimal.valueOf(150));
            given(service.updateSneaker(eq(SAMPLE_ID), any(SneakerUpdateRequest.class))).willReturn(resp);

            mvc.perform(put("/api/sneakers/{id}", SAMPLE_ID)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json.writeValueAsString(req)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.brand").value("Adidas"));
        }

        @Test
        @DisplayName("403 FORBIDDEN for USER role")
        @WithMockUser(roles = "USER")
        void forbiddenForUser() throws Exception {
            SneakerUpdateRequest req = new SneakerUpdateRequest();
            req.setName("N");
            req.setBrand("B");
            req.setPrice(BigDecimal.ONE);


            mvc.perform(put("/api/sneakers/{id}", SAMPLE_ID)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json.writeValueAsString(req)))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("200 OK for not filling the sneaker update request fields")
        @WithMockUser(roles = "OWNER")
        void badRequestForInvalidBody() throws Exception {
            SneakerUpdateRequest req = new SneakerUpdateRequest();

            mvc.perform(put("/api/sneakers/{id}", SAMPLE_ID)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json.writeValueAsString(req)))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("DELETE /api/sneakers/{id}")
    class DeleteSneaker {

        @Test
        @DisplayName("204 NO_CONTENT for ADMIN role")
        @WithMockUser(roles = "ADMIN")
        void noContentForAdmin() throws Exception {
            doNothing().when(service).deleteSneaker(SAMPLE_ID);

            mvc.perform(delete("/api/sneakers/{id}", SAMPLE_ID)
                            .with(csrf()))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("204 NO_CONTENT for OWNER role")
        @WithMockUser(roles = "OWNER")
        void forbiddenForOwner() throws Exception {
            mvc.perform(delete("/api/sneakers/{id}", SAMPLE_ID)
                            .with(csrf()))
                    .andExpect(status().isNoContent());
        }
    }
}