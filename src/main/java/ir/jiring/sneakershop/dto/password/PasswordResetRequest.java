package ir.jiring.sneakershop.dto.password;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PasswordResetRequest {
    @NotBlank(message = "phoneNumber cannot be Blank")
    @Pattern(regexp = "^(\\+98|0)?9\\d{9}$", message = "Phone number is not valid")
    private String phoneNumber;

    @NotBlank(message = "otp cannot be Blank")
    @Pattern(regexp = "^[0-9]{6}$")
    private String otp;

    @NotBlank(message = "newPassword cannot be Blank")
    @Size(min = 6)
    private String newPassword;
}

