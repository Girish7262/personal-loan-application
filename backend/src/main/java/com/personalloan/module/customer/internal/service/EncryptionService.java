package com.personalloan.module.customer.internal.service;

public interface EncryptionService {

    /**
     * Encrypts the input plain text using AES-256-GCM.
     *
     * @param plainText the plain text to encrypt
     * @return the base64-encoded encrypted payload
     */
    String encrypt(String plainText);

    /**
     * Decrypts the base64-encoded encrypted payload using AES-256-GCM.
     *
     * @param cipherText the base64-encoded encrypted payload
     * @return the decrypted plain text string
     */
    String decrypt(String cipherText);

    /**
     * Masks the Aadhaar number, showing only the last 4 digits (e.g. XXXXXXXX1234).
     *
     * @param aadhaar the raw 12-digit Aadhaar number
     * @return the masked Aadhaar string
     */
    String maskAadhaar(String aadhaar);

    /**
     * Masks the PAN number, showing only the last 4 characters (e.g. XXXXXX1234).
     *
     * @param pan the raw 10-character PAN number
     * @return the masked PAN string
     */
    String maskPan(String pan);
}
