package com.bootcamp.interviewflow.validation;

import com.bootcamp.interviewflow.dto.UpdateApplicationRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class AtLeastOneFieldImpl implements ConstraintValidator<AtLeastOneField, UpdateApplicationRequest> {

    @Override
    public boolean isValid(UpdateApplicationRequest request, ConstraintValidatorContext context) {
        if (request == null) {
            return false;
        }

        boolean hasAtLeastOneField =
                isNotBlank(request.getCompanyName()) ||
                        isNotBlank(request.getCompanyLink()) ||
                        isNotBlank(request.getPosition()) ||
                        request.getStatus() != null;

        return hasAtLeastOneField;
    }

    private boolean isNotBlank(String value) {
        return value != null && !value.trim().isEmpty();
    }
}