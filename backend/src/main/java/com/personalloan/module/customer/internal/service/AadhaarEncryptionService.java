package com.personalloan.module.customer.internal.service;

import com.personalloan.common.util.EncryptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AadhaarEncryptionService implements EncryptionService {

    private final String base64Key;

    public AadhaarEncryptionService(@Value("${app.security.encryption-key}") String base64Key) {
        this.base64Key = base64Key;
    }

    @Override
    public String encrypt(String plainText) {
        if (plainText == null || plainText.trim().isEmpty()) {
            return null;
        }
        return EncryptionUtils.encrypt(plainText.trim(), base64Key);
    }

    @Override
    public String decrypt(String cipherText) {
        if (cipherText == null || cipherText.trim().isEmpty()) {
            return null;
        }
        return EncryptionUtils.decrypt(cipherText.trim(), base64Key);
    }

    @Override
    public String maskAadhaar(String aadhaar) {
        if (aadhaar == null) {
            return null;
        }
        String trimmed = aadhaar.trim();
        if (trimmed.length() <= 4) {
            return trimmed;
        }
        return "XXXXXXXX" + trimmed.substring(trimmed.length() - 4);
    }

    @Override
    public String maskPan(String pan) {
        if (pan == null) {
            return null;
        }
        String trimmed = pan.trim();
        if (trimmed.length() <= 4) {
            return trimmed;
        }
        return "XXXXXX" + trimmed.substring(trimmed.length() - 4);
    }
}
