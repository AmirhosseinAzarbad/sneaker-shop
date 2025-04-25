package ir.jiring.sneakershop.utils;

public class PhoneNumberUtil {

    public static String normalizePhoneNumber(String phoneNumber) {
        if (phoneNumber.startsWith("+98")) {
            return "0" + phoneNumber.substring(3);
        } else if (phoneNumber.startsWith("09")) {
            return phoneNumber;
        } else {
            return "0" + phoneNumber.substring(phoneNumber.length() - 10);
        }
    }
}
