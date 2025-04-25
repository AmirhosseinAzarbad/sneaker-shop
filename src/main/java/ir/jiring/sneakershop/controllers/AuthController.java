package ir.jiring.sneakershop.controllers;

import ir.jiring.sneakershop.dto.auth.LoginRequest;
import ir.jiring.sneakershop.dto.phoneNumber.PhoneNumberRequest;
import ir.jiring.sneakershop.dto.auth.RegisterRequest;
import ir.jiring.sneakershop.dto.password.UpdatePasswordRequest;
import ir.jiring.sneakershop.models.User;
import ir.jiring.sneakershop.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }


    @PostMapping("/send-otp")
    public ResponseEntity<String> sendOtp(@Valid @RequestBody PhoneNumberRequest request) {
        authService.sendOtp(request.getPhoneNumber());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("OTP sent to phone number "+ request.getPhoneNumber() + " Please verify before registration.");
    }


    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid@RequestBody RegisterRequest request) {

        User user = authService.register(request, request.getOtp());
        return ResponseEntity.status(HttpStatus.OK).body(user.getRole()+ " registered successfully.");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid@RequestBody LoginRequest request) {
        String token = authService.login(request.getUsername(), request.getPassword());
        return ResponseEntity.ok("Login successful. Token: " + token);
    }

    @PutMapping("/update-admin-password")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<String> updateAdminPassword(@Valid@RequestBody UpdatePasswordRequest request) {
        authService.updateAdminPassword(request.getNewPassword());
        return ResponseEntity.ok("Admin registration password updated successfully.");
    }
}
