package ir.jiring.sneakershop.services;

import ir.jiring.sneakershop.dto.auth.RegisterRequest;
import ir.jiring.sneakershop.exceptions.InvalidOtpException;
import ir.jiring.sneakershop.exceptions.InvalidPasswordException;
import ir.jiring.sneakershop.exceptions.MissingPasswordException;
import ir.jiring.sneakershop.enums.Role;
import ir.jiring.sneakershop.configs.SystemConfig;
import ir.jiring.sneakershop.models.User;
import ir.jiring.sneakershop.repositories.SystemConfigRepository;
import ir.jiring.sneakershop.repositories.UserRepository;
import ir.jiring.sneakershop.security.jwt.JwtTokenProvider;
import ir.jiring.sneakershop.utils.PhoneNumberUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;


@Transactional
@Service
public class AuthService {

    @Value("${app.owner.registration.password}")
    private String ownerRegistrationPassword;

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final SystemConfigRepository systemConfigRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final OtpService otpService;

    public AuthService(
            PasswordEncoder passwordEncoder,
            UserRepository userRepository,
            SystemConfigRepository systemConfigRepository,
            JwtTokenProvider jwtTokenProvider,
            OtpService otpService) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.systemConfigRepository = systemConfigRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.otpService = otpService;
    }

    public void sendOtp(String phoneNumber) {
        otpService.generateAndSendOtp(phoneNumber);
    }

    public User register(RegisterRequest request, String otp) {

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setRole(request.getRole());

        String normalizedPhone = PhoneNumberUtil.normalizePhoneNumber(user.getPhoneNumber());
        user.setPhoneNumber(normalizedPhone);

        if (!otpService.validateOtp(normalizedPhone, otp)) {
            throw new InvalidOtpException("Invalid or expired OTP");
        }

        if (user.getRole() == Role.OWNER) {
            return registerOwner(user);
        } else if (user.getRole() == Role.ADMIN) {
            return registerAdmin(user);
        }
        if (user.getPassword() == null) {
            throw new MissingPasswordException("Password cannot be null");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        otpService.invalidateOtp(normalizedPhone);
        return userRepository.save(user);
    }

    private User registerOwner(User user) {
        if (userRepository.existsByRole(Role.OWNER)) {
            throw new IllegalStateException("Only one OWNER is allowed.");
        }
        if (!user.getPassword().equals(ownerRegistrationPassword)) {
            throw new InvalidPasswordException("Incorrect owner registration password.");
        }
        if (systemConfigRepository.count() == 0) {
            SystemConfig config = new SystemConfig(passwordEncoder.encode("adminforsneakershop"));
            systemConfigRepository.save(config);
        }
        user.setRole(Role.OWNER);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    private User registerAdmin(User user) {
        if (!userRepository.existsByRole(Role.OWNER)) {
            throw new IllegalStateException("No OWNER registered yet. ADMIN registration is not allowed.");
        }
        SystemConfig config = systemConfigRepository.findFirstByOrderByIdAsc()
                .orElseThrow(() -> new EntityNotFoundException("System config not found!"));
        if (!passwordEncoder.matches(user.getPassword(), config.getAdminRegistrationPassword())) {
            throw new InvalidPasswordException("Incorrect admin registration password.");
        }
        user.setRole(Role.ADMIN);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public String login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidPasswordException("Invalid password");
        }
        return jwtTokenProvider.createToken(username);
    }

    public void updateAdminPassword(String newPassword) {
        SystemConfig config = systemConfigRepository.findFirstByOrderByIdAsc()
                .orElseThrow(() -> new EntityNotFoundException("System config not found!.No owner registered yet"));
        config.setAdminRegistrationPassword(passwordEncoder.encode(newPassword));
        systemConfigRepository.save(config);
        List<User> adminUsers = userRepository.findByRole(Role.ADMIN);
        for (User admin : adminUsers) {
            admin.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(admin);
        }
    }
}
