package ir.jiring.sneakershop.exceptions;

public class MaxOtpRequestsExceededException extends RuntimeException {
    public MaxOtpRequestsExceededException(String message) {
        super(message);
    }
}
