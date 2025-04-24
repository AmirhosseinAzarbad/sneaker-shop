package ir.jiring.sneakershop.controllers;

import ir.jiring.sneakershop.dto.auth.LoginRequest;
import ir.jiring.sneakershop.dto.auth.RegisterRequest;
import ir.jiring.sneakershop.dto.password.UpdatePasswordRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ir.jiring.sneakershop.services.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        authService.register(request.getUser(), request.getAdminRegPassword(), request.getOwnerRegPassword());
        return ResponseEntity.ok("User registered successfully.");
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        String token = authService.login(request.getUsername(), request.getPassword());
        return ResponseEntity.ok("Login successful. Token: " + token);
    }

    @PutMapping("/update-admin-password")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<String> updateAdminPassword(@RequestBody UpdatePasswordRequest request) {
        authService.updateAdminPassword(request.getNewPassword());
        return ResponseEntity.ok("Admin registration password updated successfully.");
    }
}
