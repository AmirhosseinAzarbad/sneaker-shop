package ir.jiring.sneakershop.dto;

import lombok.Getter;
import lombok.Setter;
import ir.jiring.sneakershop.models.User;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
public class RegisterRequest {
    @NotNull
    private User user;
    private String ownerRegPassword;
    private String adminRegPassword;
}

