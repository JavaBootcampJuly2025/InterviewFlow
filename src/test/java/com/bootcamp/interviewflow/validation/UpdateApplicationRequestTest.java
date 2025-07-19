package com.bootcamp.interviewflow.validation;

import com.bootcamp.interviewflow.dto.UpdateApplicationRequest;
import com.bootcamp.interviewflow.model.ApplicationStatus;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UpdateApplicationRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void testValidRequest_WithAllFields() {
        UpdateApplicationRequest request = new UpdateApplicationRequest();
        request.setCompanyName("TechCorp");
        request.setCompanyLink("https://www.techcorp.com");
        request.setPosition("Software Developer");
        request.setStatus(ApplicationStatus.APPLIED);

        Set<ConstraintViolation<UpdateApplicationRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testValidRequest_WithSingleField_CompanyName() {
        UpdateApplicationRequest request = new UpdateApplicationRequest();
        request.setCompanyName("TechCorp");

        Set<ConstraintViolation<UpdateApplicationRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testValidRequest_WithSingleField_CompanyLink() {
        UpdateApplicationRequest request = new UpdateApplicationRequest();
        request.setCompanyLink("https://www.techcorp.com");

        Set<ConstraintViolation<UpdateApplicationRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testValidRequest_WithSingleField_Position() {
        UpdateApplicationRequest request = new UpdateApplicationRequest();
        request.setPosition("Software Developer");

        Set<ConstraintViolation<UpdateApplicationRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testValidRequest_WithSingleField_Status() {
        UpdateApplicationRequest request = new UpdateApplicationRequest();
        request.setStatus(ApplicationStatus.HR_SCREEN);

        Set<ConstraintViolation<UpdateApplicationRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testInvalidRequest_EmptyPayload() {
        UpdateApplicationRequest request = new UpdateApplicationRequest();

        Set<ConstraintViolation<UpdateApplicationRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());

        boolean hasAtLeastOneFieldViolation = violations.stream()
                .anyMatch(v -> v.getMessage().contains("At least one field must be provided"));
        assertTrue(hasAtLeastOneFieldViolation);
    }

    @Test
    void testInvalidRequest_AllFieldsNull() {
        UpdateApplicationRequest request = new UpdateApplicationRequest();
        request.setCompanyName(null);
        request.setCompanyLink(null);
        request.setPosition(null);
        request.setStatus(null);

        Set<ConstraintViolation<UpdateApplicationRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());

        boolean hasAtLeastOneFieldViolation = violations.stream()
                .anyMatch(v -> v.getMessage().contains("At least one field must be provided"));
        assertTrue(hasAtLeastOneFieldViolation);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"", "   ", "\t", "\n"})
    void testInvalidRequest_AllFieldsBlankOrEmpty(String blankValue) {
        UpdateApplicationRequest request = new UpdateApplicationRequest();
        request.setCompanyName(blankValue);
        request.setCompanyLink(blankValue);
        request.setPosition(blankValue);
        request.setStatus(null);

        Set<ConstraintViolation<UpdateApplicationRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());
    }

    @Test
    void testInvalidRequest_CompanyNameTooLong() {
        UpdateApplicationRequest request = new UpdateApplicationRequest();
        String longName = "a".repeat(256); // 256 characters, exceeds max of 255
        request.setCompanyName(longName);

        Set<ConstraintViolation<UpdateApplicationRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());

        boolean hasLengthViolation = violations.stream()
                .anyMatch(v -> v.getMessage().contains("Company name must be between 1 and 255 characters"));
        assertTrue(hasLengthViolation);
    }

    @Test
    void testInvalidRequest_PositionTooLong() {
        UpdateApplicationRequest request = new UpdateApplicationRequest();
        String longPosition = "a".repeat(256); // 256 characters, exceeds max of 255
        request.setPosition(longPosition);

        Set<ConstraintViolation<UpdateApplicationRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());

        boolean hasLengthViolation = violations.stream()
                .anyMatch(v -> v.getMessage().contains("Position must be between 1 and 255 characters"));
        assertTrue(hasLengthViolation);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "not-a-url",
            "just-text",
            "://invalid",
            "http:/",
            "ht tp://example.com", // Space in protocol
            "http://", // Missing domain
            "file://local-file", // Unsupported protocol
            "ftp://example.com", // FTP not allowed
            "http://", // Empty host
            "https://", // Empty host
            "http:// ", // Space as host
            "http://.", // Invalid domain
            "http://.com", // Invalid domain
            "www.example.com", // Missing protocol
            ""
    })
    void testInvalidRequest_InvalidCompanyLink(String invalidUrl) {
        UpdateApplicationRequest request = new UpdateApplicationRequest();
        request.setCompanyLink(invalidUrl);

        Set<ConstraintViolation<UpdateApplicationRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty(),
                "URL '" + invalidUrl + "' should be invalid but validation passed");

        boolean hasUrlViolation = violations.stream()
                .anyMatch(v -> v.getMessage().contains("Company link must be a valid URL"));
        assertTrue(hasUrlViolation,
                "Should have URL validation error for: " + invalidUrl);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "https://www.example.com",
            "http://example.com",
            "https://subdomain.example.com",
            "https://example.com/path",
            "https://example.com:8080",
            "https://example.com?query=value",
            "https://example.org",
            "http://localhost",
            "https://google.com",
            "http://test-site.co.uk",
            "https://my-company.io"
    })
    void testValidRequest_ValidCompanyLinks(String validUrl) {
        UpdateApplicationRequest request = new UpdateApplicationRequest();
        request.setCompanyLink(validUrl);

        Set<ConstraintViolation<UpdateApplicationRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty(),
                "URL '" + validUrl + "' should be valid but validation failed");
    }

    @Test
    void testValidRequest_MaxLengthCompanyName() {
        UpdateApplicationRequest request = new UpdateApplicationRequest();
        String maxLengthName = "a".repeat(255); // Exactly 255 characters
        request.setCompanyName(maxLengthName);

        Set<ConstraintViolation<UpdateApplicationRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testValidRequest_MaxLengthPosition() {
        UpdateApplicationRequest request = new UpdateApplicationRequest();
        String maxLengthPosition = "a".repeat(255); // Exactly 255 characters
        request.setPosition(maxLengthPosition);

        Set<ConstraintViolation<UpdateApplicationRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testValidRequest_AllApplicationStatuses() {
        for (ApplicationStatus status : ApplicationStatus.values()) {
            UpdateApplicationRequest request = new UpdateApplicationRequest();
            request.setStatus(status);

            Set<ConstraintViolation<UpdateApplicationRequest>> violations = validator.validate(request);
            assertTrue(violations.isEmpty(),
                    "Status " + status + " should be valid");
        }
    }

    @Test
    void testMultipleViolations() {
        UpdateApplicationRequest request = new UpdateApplicationRequest();
        request.setCompanyName(""); // Too short
        request.setCompanyLink("invalid-url"); // Invalid URL
        request.setPosition("a".repeat(256)); // Too long

        Set<ConstraintViolation<UpdateApplicationRequest>> violations = validator.validate(request);
        assertTrue(violations.size() >= 3,
                "Should have multiple validation violations");
    }

    @Test
    void testRequest_WithWhitespaceOnlyFields() {
        UpdateApplicationRequest request = new UpdateApplicationRequest();
        request.setCompanyName("   ");
        request.setPosition("\t\n  ");

        Set<ConstraintViolation<UpdateApplicationRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());

        boolean hasAtLeastOneFieldViolation = violations.stream()
                .anyMatch(v -> v.getMessage().contains("At least one field must be provided"));
        assertTrue(hasAtLeastOneFieldViolation);
    }
}