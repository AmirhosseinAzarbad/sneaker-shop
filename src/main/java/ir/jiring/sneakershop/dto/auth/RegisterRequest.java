package ir.jiring.sneakershop.dto.auth;

import ir.jiring.sneakershop.enums.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "username cannot be Blank")
    @Size(min = 3, max = 30)
    private String username;

    @NotBlank(message = "password cannot be Blank")
    @Size(min = 6)
    private String password;

    @NotBlank(message = "phoneNumber cannot be Blank")
    @Pattern(regexp = "^(\\+98|0)?9\\d{9}$", message = "Phone number is not valid")
    private String phoneNumber;

    @NotNull(message = "role cannot be Null")
    private Role role = Role.USER;

    @NotBlank(message = "otp cannot be Blank")
    @Pattern(regexp = "^[0-9]{6}$")
    private String otp;

}

