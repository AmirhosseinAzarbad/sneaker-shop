package ir.jiring.sneakershop.services;

import ir.jiring.sneakershop.exceptions.InvalidOtpException;
import ir.jiring.sneakershop.models.User;
import ir.jiring.sneakershop.repositories.UserRepository;
import ir.jiring.sneakershop.utils.PhoneNumberUtil;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PasswordResetService {

    private final UserRepository userRepository;
    private final OtpService otpService;
    private final PasswordEncoder passwordEncoder;

    public PasswordResetService(UserRepository userRepository, OtpService otpService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.otpService = otpService;
        this.passwordEncoder = passwordEncoder;
    }

    public void requestReset(String phoneNumber) {
        String normalizedPhone = PhoneNumberUtil.normalizePhoneNumber(phoneNumber);
        User user = userRepository.findByPhoneNumber(normalizedPhone)
                .orElseThrow(() -> new UsernameNotFoundException( "User not found with phone number "+ phoneNumber));

        otpService.generateAndSendOtp(normalizedPhone);
    }

    @Transactional
    public void resetPassword(String phoneNumber, String otp, String newPassword) {
        String normalizedPhone = PhoneNumberUtil.normalizePhoneNumber(phoneNumber);
        if (!otpService.validateOtp(normalizedPhone, otp)) {
            throw new InvalidOtpException("Invalid or expired OTP");
        }

        User user = userRepository.findByPhoneNumber(normalizedPhone)
                .orElseThrow(() -> new UsernameNotFoundException( "User not found with phone number "+ phoneNumber));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        otpService.invalidateOtp(normalizedPhone);
    }
}
