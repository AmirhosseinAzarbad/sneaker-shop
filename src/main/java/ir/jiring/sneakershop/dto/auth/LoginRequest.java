package ir.jiring.sneakershop.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "username cannot be Blank")
    @Size(min = 3, max = 30)
    private String username;

    @NotBlank(message = "password cannot be Blank")
    @Size(min = 6)
    private String password;
}


