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
        request.setStatus(ApplicationStatus.APPLIED);

        Set<ConstraintViolation<UpdateApplicationRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty());
    }

    @Test
    void testValidRequest_EmptyPayload() {
        UpdateApplicationRequest request = new UpdateApplicationRequest();

        Set<ConstraintViolation<UpdateApplicationRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty(), "Empty payload should be valid for update request");
    }

    @Test
    void testValidRequest_AllFieldsNull() {
        UpdateApplicationRequest request = new UpdateApplicationRequest();
        request.setCompanyName(null);
        request.setCompanyLink(null);
        request.setPosition(null);
        request.setStatus(null);

        Set<ConstraintViolation<UpdateApplicationRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty(), "All null fields should be valid for update request");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "\t", "\n"})
    void testInvalidRequest_EmptyStringsHaveSizeViolations(String emptyValue) {
        UpdateApplicationRequest request = new UpdateApplicationRequest();
        request.setCompanyName(emptyValue);
        request.setPosition(emptyValue);

        Set<ConstraintViolation<UpdateApplicationRequest>> violations = validator.validate(request);

        if (emptyValue.trim().isEmpty() && emptyValue.length() == 0) {
            // Empty string "" should fail @Size(min = 1)
            assertFalse(violations.isEmpty());
            boolean hasSizeViolations = violations.stream()
                    .anyMatch(v -> v.getMessage().contains("must be between 1 and 255 characters"));
            assertTrue(hasSizeViolations);
        } else {
            // Whitespace-only strings like "   " pass @Size(min = 1) because length > 1
            // They are technically valid according to @Size but may not be desired business logic
            assertTrue(violations.isEmpty() || violations.stream().noneMatch(
                    v -> v.getMessage().contains("must be between 1 and 255 characters")));
        }
    }

    @Test
    void testInvalidRequest_EmptyStringFields() {
        UpdateApplicationRequest request = new UpdateApplicationRequest();
        request.setCompanyName(""); // Empty string should fail @Size(min = 1)
        request.setPosition(""); // Empty string should fail @Size(min = 1)

        Set<ConstraintViolation<UpdateApplicationRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty());

        boolean hasSizeViolations = violations.stream()
                .anyMatch(v -> v.getMessage().contains("must be between 1 and 255 characters"));
        assertTrue(hasSizeViolations);
    }

    @Test
    void testValidRequest_NullFields() {
        UpdateApplicationRequest request = new UpdateApplicationRequest();
        request.setCompanyName(null); // Null values are valid for @Size
        request.setPosition(null); // Null values are valid for @Size

        Set<ConstraintViolation<UpdateApplicationRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty(), "Null values should be valid for @Size validation");
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
            "https://", // Empty host
            "file://local-file", // Unsupported protocol
            "ftp://example.com", // FTP not allowed
            "http://.", // Invalid domain
            "http://.com", // Invalid domain
            "www.example.com", // Missing protocol
            "https://-example.com", // Domain starts with hyphen
            "https://example-.com", // Domain ends with hyphen
            "http://example", // Missing TLD (not localhost)
            "https://test", // Missing TLD (not localhost)
            "http://example.com:abc", // Invalid port
            "https://example.com:-80" // Negative port
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
            "https://example.com?query=value", // Now supported!
            "https://example.com/path?query=value",
            "https://example.com/path?query=value&param=test",
            "https://example.com#fragment",
            "https://example.com/path#fragment",
            "https://example.com/path?query=value#fragment",
            "https://example.org",
            "http://localhost",
            "http://localhost:3000",
            "https://localhost:8080/api",
            "https://google.com",
            "http://test-site.co.uk",
            "https://my-company.io",
            "https://api-v2.example.com",
            "http://sub1.sub2.example.com",
            "https://example123.com",
            "http://123example.org"
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
    void testValidRequest_WithWhitespaceOnlyFields() {
        UpdateApplicationRequest request = new UpdateApplicationRequest();
        request.setCompanyName("   "); // 3 characters - passes @Size(min = 1)
        request.setPosition("\t\n  "); // 4 characters - passes @Size(min = 1)

        Set<ConstraintViolation<UpdateApplicationRequest>> violations = validator.validate(request);
        // @Size(min = 1) only checks length, not content - whitespace strings pass
        assertTrue(violations.isEmpty(),
                "Whitespace-only strings pass @Size(min = 1) validation because length > 1");
    }

    // Additional tests for new pattern features
    @Test
    void testValidRequest_LocalhostVariations() {
        String[] localhostUrls = {
                "http://localhost",
                "https://localhost",
                "http://localhost:3000",
                "https://localhost:8080",
                "http://localhost/api",
                "https://localhost:8080/api/v1?debug=true"
        };

        for (String url : localhostUrls) {
            UpdateApplicationRequest request = new UpdateApplicationRequest();
            request.setCompanyLink(url);

            Set<ConstraintViolation<UpdateApplicationRequest>> violations = validator.validate(request);
            assertTrue(violations.isEmpty(),
                    "Localhost URL should be valid: " + url);
        }
    }

    @Test
    void testValidRequest_QueryParametersAndFragments() {
        String[] urlsWithQueryAndFragment = {
                "https://example.com?param=value",
                "https://example.com?param1=value1&param2=value2",
                "https://example.com#section",
                "https://example.com/path?query=test#anchor",
                "https://api.example.com/v1/users?limit=10&offset=0",
                "https://docs.example.com/guide#installation"
        };

        for (String url : urlsWithQueryAndFragment) {
            UpdateApplicationRequest request = new UpdateApplicationRequest();
            request.setCompanyLink(url);

            Set<ConstraintViolation<UpdateApplicationRequest>> violations = validator.validate(request);
            assertTrue(violations.isEmpty(),
                    "URL with query/fragment should be valid: " + url);
        }
    }

    @Test
    void testInvalidRequest_DomainLengthLimits() {
        UpdateApplicationRequest request = new UpdateApplicationRequest();
        // Create a domain label longer than 63 characters
        String longLabel = "a".repeat(64);
        String invalidUrl = "https://" + longLabel + ".com";
        request.setCompanyLink(invalidUrl);

        Set<ConstraintViolation<UpdateApplicationRequest>> violations = validator.validate(request);
        assertFalse(violations.isEmpty(),
                "Domain with label >63 characters should be invalid");

        boolean hasUrlViolation = violations.stream()
                .anyMatch(v -> v.getMessage().contains("Company link must be a valid URL"));
        assertTrue(hasUrlViolation);
    }
}