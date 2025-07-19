package com.bootcamp.interviewflow.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidUsernameImpl implements ConstraintValidator<ValidUsername, String> {

    @Override
    public boolean isValid(String username, ConstraintValidatorContext context) {
        if (username == null) {
            return true;
        }
        // Username can only contain letters (a-z, A-Z) and spaces
        return username.matches("^[a-zA-Z\\s]+$");
    }
}