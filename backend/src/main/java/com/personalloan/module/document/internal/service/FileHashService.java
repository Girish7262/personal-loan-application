package com.personalloan.module.document.internal.service;

import com.personalloan.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
@Slf4j
public class FileHashService {

    /**
     * Calculates the SHA-256 hash of a file byte payload, returning it as a hex string.
     */
    public String calculateSha256(byte[] fileData) {
        if (fileData == null) {
            throw new BusinessException("File payload is empty. Cannot compute hash.");
        }

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(fileData);
            
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            
            String fileHash = hexString.toString();
            log.debug("Computed SHA-256 file hash: {}", fileHash);
            return fileHash;
        } catch (NoSuchAlgorithmException e) {
            log.error("SHA-256 algorithm not found", e);
            throw new RuntimeException("SHA-256 digest algorithm missing", e);
        }
    }
}
