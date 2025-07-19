package com.bootcamp.interviewflow.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class StrongPasswordImpl implements ConstraintValidator<StrongPassword, String> {

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        // Null values should be handled by @NotBlank
        if (password == null) {
            return true;
        }

        // Check for at least one uppercase letter
        boolean hasUppercase = password.matches(".*[A-Z].*");

        // Check for at least one lowercase letter
        boolean hasLowercase = password.matches(".*[a-z].*");

        // Check for at least one digit
        boolean hasDigit = password.matches(".*\\d.*");

        // Check for at least one special character
        boolean hasSpecialChar = password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*");

        return hasUppercase && hasLowercase && hasDigit && hasSpecialChar;
    }
}