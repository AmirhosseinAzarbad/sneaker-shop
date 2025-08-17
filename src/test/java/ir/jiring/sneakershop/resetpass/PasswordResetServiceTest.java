package ir.jiring.sneakershop.resetpass;

import ir.jiring.sneakershop.exceptions.InvalidOtpException;
import ir.jiring.sneakershop.models.User;
import ir.jiring.sneakershop.repositories.jpa.UserRepository;
import ir.jiring.sneakershop.services.OtpService;
import ir.jiring.sneakershop.services.PasswordResetService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private OtpService otpService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PasswordResetService passwordResetService;

    private static final String PHONE = "1234567890";
    private static final String OTP = "000000";
    private static final String NEW_PASSWORD = "newPass";
    private static final String ENCODED_PASSWORD = "encodedPass";

    @Nested
    @DisplayName("requestReset")
    class RequestResetTests {
        @Test
        @DisplayName("should generate and send OTP when user exists")
        void shouldGenerateAndSendOtp_WhenUserExists() {
            User user = new User();
            user.setPhoneNumber(PHONE);
            given(userRepository.findByPhoneNumber(anyString())).willReturn(Optional.of(user));

            passwordResetService.requestReset(PHONE);

            then(otpService).should(times(1)).generateAndSendOtp(anyString());
        }

        @Test
        @DisplayName("should throw UsernameNotFoundException when user does not exist")
        void shouldThrow_WhenUserNotFound() {
            given(userRepository.findByPhoneNumber(anyString())).willReturn(Optional.empty());

            assertThrows(UsernameNotFoundException.class, () -> passwordResetService.requestReset(PHONE));

            then(otpService).shouldHaveNoInteractions();
        }
    }

    @Nested
    @DisplayName("resetPassword")
    class ResetPasswordTests {
        @Test
        @DisplayName("should reset password when OTP is valid and user exists")
        void shouldResetPassword_WhenOtpValidAndUserExists() {
            User user = new User();
            user.setPhoneNumber(PHONE);
            user.setPassword("oldPass");

            given(otpService.validateOtp(anyString(), eq(OTP))).willReturn(true);
            given(userRepository.findByPhoneNumber(anyString())).willReturn(Optional.of(user));
            given(passwordEncoder.encode(NEW_PASSWORD)).willReturn(ENCODED_PASSWORD);

            passwordResetService.resetPassword(PHONE, OTP, NEW_PASSWORD);

            then(passwordEncoder).should().encode(NEW_PASSWORD);
            then(userRepository).should().save(user);
            assertEquals(ENCODED_PASSWORD, user.getPassword());
            then(otpService).should().invalidateOtp(anyString());
        }

        @Test
        @DisplayName("should throw InvalidOtpException when OTP is invalid")
        void shouldThrowInvalidOtp_WhenOtpInvalid() {
            given(otpService.validateOtp(anyString(), eq(OTP))).willReturn(false);

            assertThrows(InvalidOtpException.class, () ->
                    passwordResetService.resetPassword(PHONE, OTP, NEW_PASSWORD)
            );

            then(userRepository).shouldHaveNoInteractions();
            then(passwordEncoder).shouldHaveNoInteractions();
            then(otpService).should(never()).invalidateOtp(anyString());
        }

        @Test
        @DisplayName("should throw UsernameNotFoundException when user does not exist even if OTP is valid")
        void shouldThrowUsernameNotFound_WhenUserNotFound() {
            given(otpService.validateOtp(anyString(), eq(OTP))).willReturn(true);
            given(userRepository.findByPhoneNumber(anyString())).willReturn(Optional.empty());

            assertThrows(UsernameNotFoundException.class, () ->
                    passwordResetService.resetPassword(PHONE, OTP, NEW_PASSWORD)
            );

            then(passwordEncoder).shouldHaveNoInteractions();
            then(otpService).should(never()).invalidateOtp(anyString());
        }
    }
}
