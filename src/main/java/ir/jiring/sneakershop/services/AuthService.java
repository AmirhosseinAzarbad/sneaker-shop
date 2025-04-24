package ir.jiring.sneakershop.services;

import ir.jiring.sneakershop.enums.Role;
import ir.jiring.sneakershop.configs.SystemConfig;
import ir.jiring.sneakershop.models.User;
import ir.jiring.sneakershop.repositories.SystemConfigRepository;
import ir.jiring.sneakershop.repositories.UserRepository;
import ir.jiring.sneakershop.security.jwt.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class AuthService {

    @Value("${app.owner.registration.password}")
    private String ownerRegistrationPassword;

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final SystemConfigRepository systemConfigRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthService(PasswordEncoder passwordEncoder,
                       UserRepository userRepository,
                       SystemConfigRepository systemConfigRepository,
                       JwtTokenProvider jwtTokenProvider) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.systemConfigRepository = systemConfigRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public User register(User user, String adminRegPassword, String ownerRegPassword) {
        if (user.getRole() == Role.OWNER) {
            registerOwner(user, ownerRegPassword);
        } else if (user.getRole() == Role.ADMIN) {
            registerAdmin(user, adminRegPassword);
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    private void registerOwner(User user, String ownerRegPassword) {
        if (userRepository.existsByRole(Role.OWNER)) {
            throw new RuntimeException("Only one OWNER is allowed.");
        }
        if (!ownerRegPassword.equals(ownerRegistrationPassword)) {
            throw new RuntimeException("Incorrect owner registration password.");
        }
        if (systemConfigRepository.count() == 0) {
            SystemConfig config = new SystemConfig("adminforsneakershop");
            systemConfigRepository.save(config);
        }
    }

    private void registerAdmin(User user, String adminRegPassword) {
        if (!userRepository.existsByRole(Role.OWNER)) {
            throw new RuntimeException("No OWNER registered yet. ADMIN registration is not allowed.");
        }
        SystemConfig config = systemConfigRepository.findFirstByOrderByIdAsc()
                .orElseThrow(() -> new RuntimeException("System config not found!"));
        if (!adminRegPassword.equals(config.getAdminRegistrationPassword())) {
            throw new RuntimeException("Incorrect admin registration password.");
        }
    }

    public String login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }
        return jwtTokenProvider.createToken(username);
    }

    public void updateAdminPassword(String newPassword) {
        SystemConfig config = systemConfigRepository.findFirstByOrderByIdAsc()
                .orElseThrow(() -> new RuntimeException("System config not found!"));
        config.setAdminRegistrationPassword(newPassword);
        systemConfigRepository.save(config);
    }
}
