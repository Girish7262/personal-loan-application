package com.personalloan.module.auth.internal.service;

import com.personalloan.common.exception.BusinessException;
import org.springframework.stereotype.Service;

@Service
public class PasswordPolicyService {

    /**
     * Validates if a password matches confirmation requirements and meets security strength rules:
     * Minimum 8 characters, at least 1 uppercase letter, 1 lowercase letter, 1 digit, and 1 special symbol (@$!%*?&).
     *
     * @param password the password to validate
     * @param confirmPassword the confirmation password input
     * @throws BusinessException if passwords mismatch or do not meet policy strength rules
     */
    public void validatePassword(String password, String confirmPassword) {
        if (password == null || confirmPassword == null) {
            throw new BusinessException("Password fields cannot be null");
        }
        if (!password.equals(confirmPassword)) {
            throw new BusinessException("Passwords do not match");
        }
        if (password.length() < 8) {
            throw new BusinessException("Password must be at least 8 characters long");
        }
        boolean hasUppercase = false;
        boolean hasLowercase = false;
        boolean hasDigit = false;
        boolean hasSpecial = false;
        String specialChars = "@$!%*?&";

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) hasUppercase = true;
            else if (Character.isLowerCase(c)) hasLowercase = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else if (specialChars.indexOf(c) >= 0) hasSpecial = true;
        }

        if (!hasUppercase || !hasLowercase || !hasDigit || !hasSpecial) {
            throw new BusinessException("Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character (@$!%*?&)");
        }
    }
}
