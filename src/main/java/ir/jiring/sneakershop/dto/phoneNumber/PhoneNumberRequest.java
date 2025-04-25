package ir.jiring.sneakershop.dto.phoneNumber;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class PhoneNumberRequest {

    @NotBlank(message = "phoneNumber cannot be Blank")
    @Pattern(regexp = "^(\\+98|0)?9\\d{9}$", message = "Phone number is not valid")
    private String phoneNumber;
}
