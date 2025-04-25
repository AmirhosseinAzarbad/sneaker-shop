package ir.jiring.sneakershop.controllers;

import ir.jiring.sneakershop.dto.password.PasswordResetRequest;
import ir.jiring.sneakershop.dto.phoneNumber.PhoneNumberRequest;
import ir.jiring.sneakershop.services.PasswordResetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth/password-reset")
@RequiredArgsConstructor
public class PasswordResetController {

    private final PasswordResetService resetService;

    @PostMapping("/request")
    public ResponseEntity<?> requestReset(@Valid @RequestBody PhoneNumberRequest request) {
        resetService.requestReset(request.getPhoneNumber());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body("Password reset confirmation sent to "+request.getPhoneNumber());
    }

    @PostMapping("/confirm")
    public ResponseEntity<?> confirmReset(@Valid@RequestBody PasswordResetRequest request) {
        resetService.resetPassword(request.getPhoneNumber(), request.getOtp(), request.getNewPassword());
        return ResponseEntity.status(HttpStatus.OK).body("Password changed successfully");
    }

}
