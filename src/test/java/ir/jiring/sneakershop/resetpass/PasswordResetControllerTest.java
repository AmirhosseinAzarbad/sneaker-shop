package ir.jiring.sneakershop.resetpass;
import com.fasterxml.jackson.databind.ObjectMapper;
import ir.jiring.sneakershop.dto.password.PasswordResetRequest;
import ir.jiring.sneakershop.dto.phoneNumber.PhoneNumberRequest;
import ir.jiring.sneakershop.exceptions.InvalidOtpException;
import ir.jiring.sneakershop.security.jwt.JwtTokenProvider;
import ir.jiring.sneakershop.services.PasswordResetService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PasswordResetControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    @Qualifier("objectMapper")
    private ObjectMapper json;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @MockitoBean
    private PasswordResetService resetService;

    @Nested
    @DisplayName("POST /auth/password-reset/request")
    class RequestReset {
        @Test
        @DisplayName("returns 202 Accepted for valid phone number")
        void validPhoneNumber_returnsAccepted() throws Exception {
            PhoneNumberRequest req = new PhoneNumberRequest();
            req.setPhoneNumber("09123456789");

            mvc.perform(post("/auth/password-reset/request")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json.writeValueAsString(req)))
                    .andExpect(status().isAccepted())
                    .andExpect(content().string("Password reset confirmation sent to 09123456789"));
        }

        @Test
        @DisplayName("returns 400 Bad Request for blank phone number")
        void blankPhoneNumber_returnsBadRequest() throws Exception {
            PhoneNumberRequest req = new PhoneNumberRequest();
            req.setPhoneNumber("");

            mvc.perform(post("/auth/password-reset/request")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json.writeValueAsString(req)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("returns 404 Not Found when user not found")
        void userNotFound_throwsInternalError() throws Exception {
            willThrow(new UsernameNotFoundException("User not found"))
                    .given(resetService).requestReset("09123456789");

            PhoneNumberRequest req = new PhoneNumberRequest();
            req.setPhoneNumber("09123456789");
            mvc.perform(post("/auth/password-reset/request")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json.writeValueAsString(req)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    @DisplayName("POST /auth/password-reset/confirm")
    class ConfirmReset {
        @Test
        @DisplayName("returns 200 OK for valid request")
        void validRequest_returnsOk() throws Exception {
            PasswordResetRequest req = new PasswordResetRequest();
            req.setPhoneNumber("09123456789");
            req.setOtp("123456");
            req.setNewPassword("newPass");

            mvc.perform(post("/auth/password-reset/confirm")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json.writeValueAsString(req)))
                    .andExpect(status().isOk())
                    .andExpect(content().string("Password changed successfully"));
        }

        @Test
        @DisplayName("returns 400 Bad Request for missing fields")
        void missingFields_returnsBadRequest() throws Exception {
            // missing otp and newPassword
            String jsonBody = "{\"phoneNumber\":\"09123456789\"}";

            mvc.perform(post("/auth/password-reset/confirm")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(jsonBody))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("returns 401 Unauthorized for invalid OTP")
        void invalidOtp_throwsInternalError() throws Exception {
            willThrow(new InvalidOtpException("Invalid OTP"))
                    .given(resetService).resetPassword("09123456789", "000000", "newPass");

            PasswordResetRequest req = new PasswordResetRequest();
            req.setPhoneNumber("09123456789");
            req.setOtp("000000");
            req.setNewPassword("newPass");

            mvc.perform(post("/auth/password-reset/confirm")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json.writeValueAsString(req)))
                    .andExpect(status().isUnauthorized());
        }
    }
}

