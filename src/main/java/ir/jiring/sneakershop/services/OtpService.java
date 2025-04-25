package ir.jiring.sneakershop.services;

import ir.jiring.sneakershop.utils.PhoneNumberUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class OtpService {

    private final Map<String, OtpData> otpStorage = new ConcurrentHashMap<>();
    private final Map<String, AtomicInteger> otpRequestCount = new ConcurrentHashMap<>();

    @Value("${otp.expiryTime}")
    private long otpExpiryTime;

    @Value("${otp.maxRequests}")
    private int maxOtpRequests;

    public void generateAndSendOtp(String phoneNumber) {
        String normalizedPhone = PhoneNumberUtil.normalizePhoneNumber(phoneNumber);

        otpRequestCount.compute(normalizedPhone, (key,count) -> {
            if (count == null) {
                return new AtomicInteger(0);
            }else {
                if(count.get() >= maxOtpRequests) {
                    throw new RuntimeException("Max OTP requests exceeded.");
                }
                count.incrementAndGet();
                return count;
            }
        });


        String otp = String.valueOf((int) (Math.random() * 900000) + 100000);
        otpStorage.put(normalizedPhone, new OtpData(otp, LocalDateTime.now().plusMinutes(otpExpiryTime)));

        log.info("Sending OTP {} to phone number {}", otp, normalizedPhone);
    }

    public boolean validateOtp(String phoneNumber, String otp) {
        String normalizedPhone = PhoneNumberUtil.normalizePhoneNumber(phoneNumber);
        OtpData data = otpStorage.get(normalizedPhone);

        if (data == null || LocalDateTime.now().isAfter(data.expiry)) {
            return false;
        }

        return data.otp.equals(otp);
    }

    public void invalidateOtp(String phoneNumber) {
        String normalizedPhone = PhoneNumberUtil.normalizePhoneNumber(phoneNumber);
        otpStorage.remove(normalizedPhone);
        otpRequestCount.remove(normalizedPhone);
    }

    private static class OtpData {
        String otp;
        LocalDateTime expiry;

        public OtpData(String otp, LocalDateTime expiry) {
            this.otp = otp;
            this.expiry = expiry;
        }
    }
}
