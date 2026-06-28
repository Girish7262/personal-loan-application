package com.personalloan.module.customer.internal.service;

import com.personalloan.common.exception.BusinessException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.regex.Pattern;

@Service
public class ValidationService {

    private static final Pattern PAN_PATTERN = Pattern.compile("^[A-Z]{5}[0-9]{4}[A-Z]{1}$");
    private static final Pattern AADHAAR_PATTERN = Pattern.compile("^[0-9]{12}$");
    private static final Pattern MOBILE_PATTERN = Pattern.compile("^[6-9][0-9]{9}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    /**
     * Validates that the customer is between 21 and 65 years of age.
     *
     * @param dob the date of birth
     */
    public void validateAge(LocalDate dob) {
        if (dob == null) {
            throw new BusinessException("Date of birth is required");
        }
        int age = Period.between(dob, LocalDate.now()).getYears();
        if (age < 21 || age > 65) {
            throw new BusinessException("Age must be between 21 and 65 years");
        }
    }

    /**
     * Validates the PAN format.
     *
     * @param pan the PAN number string
     */
    public void validatePan(String pan) {
        if (pan == null || !PAN_PATTERN.matcher(pan.trim()).matches()) {
            throw new BusinessException("Invalid PAN format. Must be 10 characters alphanumeric (e.g. ABCDE1234F)");
        }
    }

    /**
     * Validates the raw 12-digit Aadhaar format.
     *
     * @param aadhaar the raw Aadhaar number string
     */
    public void validateAadhaar(String aadhaar) {
        if (aadhaar == null || !AADHAAR_PATTERN.matcher(aadhaar.trim()).matches()) {
            throw new BusinessException("Invalid Aadhaar format. Must be a 12-digit numeric sequence (e.g. 123412341234)");
        }
    }

    /**
     * Validates the mobile format.
     *
     * @param mobile the mobile number string
     */
    public void validateMobile(String mobile) {
        if (mobile == null || !MOBILE_PATTERN.matcher(mobile.trim()).matches()) {
            throw new BusinessException("Invalid mobile format. Must be a 10-digit Indian mobile number (e.g. 9876543210)");
        }
    }

    /**
     * Validates the email format.
     *
     * @param email the email address string
     */
    public void validateEmail(String email) {
        if (email == null || !EMAIL_PATTERN.matcher(email.trim()).matches()) {
            throw new BusinessException("Invalid email format");
        }
    }
}
