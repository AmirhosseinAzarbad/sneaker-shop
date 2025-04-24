package ir.jiring.sneakershop.dto.password;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdatePasswordRequest {
    @NotNull(message = "New password cannot be null")
    private String newPassword;
}

