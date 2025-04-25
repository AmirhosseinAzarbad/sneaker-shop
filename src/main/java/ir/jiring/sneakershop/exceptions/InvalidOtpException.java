package ir.jiring.sneakershop.exceptions;

import lombok.Getter;

@Getter
public class InvalidOtpException extends RuntimeException {


    public InvalidOtpException(String message) {
        super(message);
    }

}
