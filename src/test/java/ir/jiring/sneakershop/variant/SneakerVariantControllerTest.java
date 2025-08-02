package ir.jiring.sneakershop.variant;


import com.fasterxml.jackson.databind.ObjectMapper;
import ir.jiring.sneakershop.dto.variant.SneakerVariantAddAndUpdateResponse;
import ir.jiring.sneakershop.dto.variant.SneakerVariantAddRequest;
import ir.jiring.sneakershop.dto.variant.SneakerVariantGetAllResponse;
import ir.jiring.sneakershop.dto.variant.SneakerVariantUpdateRequest;
import ir.jiring.sneakershop.services.SneakerVariantService;
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
import java.util.List;
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
class SneakerVariantControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    @Qualifier("objectMapper")
    private ObjectMapper json;

    @MockitoBean
    private SneakerVariantService service;

    // Required for security filter
    @MockitoBean
    private ir.jiring.sneakershop.security.jwt.JwtTokenProvider jwtTokenProvider;
    @MockitoBean
    private ir.jiring.sneakershop.repositories.UserRepository userRepository;

    private final UUID SAMPLE_SNEAKER_ID = UUID.randomUUID();
    private final UUID SAMPLE_VARIANT_ID = UUID.randomUUID();

    @Nested
    @DisplayName("POST /api/sneaker-variants/add/{sneakerId}")
    class AddVariant {

        @Test
        @DisplayName("201 CREATED for ADMIN with valid payload")
        @WithMockUser(roles = "ADMIN")
        void createdForAdmin() throws Exception {
            SneakerVariantAddRequest req = new SneakerVariantAddRequest();
            req.setSize("42");
            req.setColor("Black");
            req.setPrice(BigDecimal.valueOf(150));
            req.setStockQuantity(10);

            SneakerVariantAddAndUpdateResponse resp = new SneakerVariantAddAndUpdateResponse();
            resp.setId(SAMPLE_VARIANT_ID);
            resp.setSize("42");
            resp.setColor("Black");
            resp.setPrice(BigDecimal.valueOf(150));
            resp.setStockQuantity(10);

            given(service.addVariant(any(SneakerVariantAddRequest.class), eq(SAMPLE_SNEAKER_ID)))
                    .willReturn(resp);

            mvc.perform(post("/api/sneaker-variants/add/{id}", SAMPLE_SNEAKER_ID)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json.writeValueAsString(req)))
                    .andExpect(status().isCreated())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id").value(SAMPLE_VARIANT_ID.toString()))
                    .andExpect(jsonPath("$.color").value("Black"));
        }

        @Test
        @DisplayName("403 FORBIDDEN for USER role")
        @WithMockUser(roles = "USER")
        void forbiddenForUser() throws Exception {
            SneakerVariantAddRequest req = new SneakerVariantAddRequest();
            req.setSize("40");
            req.setColor("Red");
            req.setPrice(BigDecimal.valueOf(120));
            req.setStockQuantity(5);

            mvc.perform(post("/api/sneaker-variants/add/{id}", SAMPLE_SNEAKER_ID)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json.writeValueAsString(req)))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("400 BAD_REQUEST for invalid payload")
        @WithMockUser(roles = "OWNER")
        void badRequestForInvalidBody() throws Exception {
            SneakerVariantAddRequest req = new SneakerVariantAddRequest();
            mvc.perform(post("/api/sneaker-variants/add/{id}", SAMPLE_SNEAKER_ID)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json.writeValueAsString(req)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("401 UNAUTHORIZED when not authenticated")
        void unauthorizedWhenNoAuth() throws Exception {
            SneakerVariantAddRequest req = new SneakerVariantAddRequest();
            req.setSize("38");
            req.setColor("White");
            req.setPrice(BigDecimal.valueOf(100));
            req.setStockQuantity(3);

            mvc.perform(post("/api/sneaker-variants/add/{id}", SAMPLE_SNEAKER_ID)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json.writeValueAsString(req)))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Test
    @DisplayName("GET /api/sneaker-variants/show/{sneakerId} → 200 OK public")
    void listPublicVariants() throws Exception {
        SneakerVariantGetAllResponse.VariantsList item = new SneakerVariantGetAllResponse.VariantsList();
        item.setId(SAMPLE_VARIANT_ID);
        item.setSize("41");
        item.setColor("Blue");
        item.setPrice(BigDecimal.valueOf(130));
        item.setStockQuantity(7);

        SneakerVariantGetAllResponse resp = new SneakerVariantGetAllResponse();
        resp.setSneakerName("Air Max");
        resp.setSneakerBrand("Nike");
        resp.setVariants(List.of(item));

        given(service.getVariants(SAMPLE_SNEAKER_ID)).willReturn(resp);

        mvc.perform(get("/api/sneaker-variants/show/{id}", SAMPLE_SNEAKER_ID)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.sneakerName").value("Air Max"))
                .andExpect(jsonPath("$.sneakerBrand").value("Nike"))
                .andExpect(jsonPath("$.variants[0].id").value(SAMPLE_VARIANT_ID.toString()))
                .andExpect(jsonPath("$.variants[0].color").value("Blue"))
                .andExpect(jsonPath("$.variants[0].size").value("41"))
                .andExpect(jsonPath("$.variants[0].price").value(130))
                .andExpect(jsonPath("$.variants[0].stockQuantity").value(7));
    }

    @Nested
    @DisplayName("PUT /api/sneaker-variants/{variantId}")
    class UpdateVariant {

        @Test
        @DisplayName("200 OK for OWNER with valid payload")
        @WithMockUser(roles = "OWNER")
        void okForOwner() throws Exception {
            SneakerVariantUpdateRequest req = new SneakerVariantUpdateRequest();
            req.setColor("Green");
            req.setStockQuantity(15);

            SneakerVariantAddAndUpdateResponse resp = new SneakerVariantAddAndUpdateResponse();
            resp.setId(SAMPLE_VARIANT_ID);
            resp.setColor("Green");
            resp.setStockQuantity(15);

            given(service.updateSneakerVariant(eq(SAMPLE_VARIANT_ID), any(SneakerVariantUpdateRequest.class)))
                    .willReturn(resp);

            mvc.perform(put("/api/sneaker-variants/{id}", SAMPLE_VARIANT_ID)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json.writeValueAsString(req)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.stockQuantity").value(15));
        }

        @Test
        @DisplayName("403 FORBIDDEN for USER role")
        @WithMockUser(roles = "USER")
        void forbiddenForUser() throws Exception {
            SneakerVariantUpdateRequest req = new SneakerVariantUpdateRequest();
            req.setSize("39");

            mvc.perform(put("/api/sneaker-variants/{id}", SAMPLE_VARIANT_ID)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json.writeValueAsString(req)))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("200 Ok for invalid payload")
        @WithMockUser(roles = "ADMIN")
        void badRequestForInvalidBody() throws Exception {
            mvc.perform(put("/api/sneaker-variants/{id}", SAMPLE_VARIANT_ID)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("DELETE /api/sneaker-variants/{variantId}")
    class DeleteVariant {

        @Test
        @DisplayName("204 NO_CONTENT for ADMIN")
        @WithMockUser(roles = "ADMIN")
        void noContentForAdmin() throws Exception {
            doNothing().when(service).deleteVariant(SAMPLE_VARIANT_ID);

            mvc.perform(delete("/api/sneaker-variants/{id}", SAMPLE_VARIANT_ID)
                            .with(csrf()))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("204 NO_CONTENT for OWNER")
        @WithMockUser(roles = "OWNER")
        void noContentForOwner() throws Exception {
            doNothing().when(service).deleteVariant(SAMPLE_VARIANT_ID);

            mvc.perform(delete("/api/sneaker-variants/{id}", SAMPLE_VARIANT_ID)
                            .with(csrf()))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("403 FORBIDDEN for USER")
        @WithMockUser(roles = "USER")
        void forbiddenForUser() throws Exception {
            mvc.perform(delete("/api/sneaker-variants/{id}", SAMPLE_VARIANT_ID)
                            .with(csrf()))
                    .andExpect(status().isForbidden());
        }
    }
}
