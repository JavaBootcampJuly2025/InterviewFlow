package com.bootcamp.interviewflow.validation;


import com.bootcamp.interviewflow.dto.LoginRequest;
import com.bootcamp.interviewflow.dto.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ValidationTest {

    private Validator validator;
    private ValidUsernameImpl usernameImpl;
    private StrongPasswordImpl passwordImpl;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        usernameImpl = new ValidUsernameImpl();
        passwordImpl = new StrongPasswordImpl();
    }

    @Test
    @DisplayName("Valid RegisterRequest should pass validation")
    void testValidRegisterRequest() {
        RegisterRequest request = new RegisterRequest("John Doe", "john@example.com", "StrongPass123!");

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty(), "Valid request should not have validation errors");
    }

    @Test
    @DisplayName("Valid LoginRequest should pass validation")
    void testValidLoginRequest() {
        LoginRequest request = new LoginRequest("john@example.com", "StrongPass123!");

        Set<ConstraintViolation<LoginRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty(), "Valid request should not have validation errors");
    }

    // Username validation tests
    @ParameterizedTest
    @ValueSource(strings = {"John", "Jane Doe", "Mary Ann Smith", "A B", "abcd efgh"})
    @DisplayName("Valid usernames should pass validation")
    void testValidUsernames(String username) {
        assertTrue(usernameImpl.isValid(username, null),
                "Username '" + username + "' should be valid");
    }

    @ParameterizedTest
    @ValueSource(strings = {"John123", "Jane-Doe", "Mary@Ann", "user_name", "John.Doe", "user!name"})
    @DisplayName("Invalid usernames should fail validation")
    void testInvalidUsernames(String username) {
        assertFalse(usernameImpl.isValid(username, null),
                "Username '" + username + "' should be invalid");
    }

    @Test
    @DisplayName("Username at max length (50 characters) should be valid")
    void testUsernameMaxLength() {
        String maxLengthUsername = "A".repeat(50); // 50 'A's
        RegisterRequest request = new RegisterRequest(maxLengthUsername, "test@example.com", "StrongPass123!");

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty(), "Username at max length should be valid");
    }

    @Test
    @DisplayName("Username over max length (51 characters) should fail validation")
    void testUsernameOverMaxLength() {
        String overMaxUsername = "A".repeat(51); // 51 'A's
        RegisterRequest request = new RegisterRequest(overMaxUsername, "test@example.com", "StrongPass123!");

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty(), "Username over max length should fail validation");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("username")));
    }

    @Test
    @DisplayName("Username at min length (2 characters) should be valid")
    void testUsernameMinLength() {
        RegisterRequest request = new RegisterRequest("Jo", "test@example.com", "StrongPass123!");

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty(), "Username at min length should be valid");
    }

    @Test
    @DisplayName("Username under min length (1 character) should fail validation")
    void testUsernameUnderMinLength() {
        RegisterRequest request = new RegisterRequest("J", "test@example.com", "StrongPass123!");

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty(), "Username under min length should fail validation");
    }

    @Test
    @DisplayName("Blank username should fail validation")
    void testBlankUsername() {
        RegisterRequest request = new RegisterRequest("", "test@example.com", "StrongPass123!");

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty(), "Blank username should fail validation");
    }

    // Password validation tests
    @ParameterizedTest
    @ValueSource(strings = {"StrongPass123!", "MyP@ssw0rd", "Secure123$", "Valid1@"})
    @DisplayName("Valid strong passwords should pass validation")
    void testValidStrongPasswords(String password) {
        assertTrue(passwordImpl.isValid(password, null),
                "Password '" + password + "' should be valid");
    }

    @ParameterizedTest
    @ValueSource(strings = {"weak", "password", "PASSWORD", "12345678", "Pass123", "strongpass!", "STRONGPASS123!"})
    @DisplayName("Weak passwords should fail validation")
    void testWeakPasswords(String password) {
        assertFalse(passwordImpl.isValid(password, null),
                "Password '" + password + "' should be invalid");
    }

    @Test
    @DisplayName("Password at max length (255 characters) should be valid")
    void testPasswordMaxLength() {
        // Create a 255-character password that meets strength requirements
        String maxLengthPassword = "StrongPass123!" + "A".repeat(241); // 14 + 241 = 255
        RegisterRequest request = new RegisterRequest("John Doe", "test@example.com", maxLengthPassword);

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty(), "Password at max length should be valid");
    }

    @Test
    @DisplayName("Password over max length (256 characters) should fail validation")
    void testPasswordOverMaxLength() {
        // Create a 256-character password
        String overMaxPassword = "StrongPass123!" + "A".repeat(242); // 14 + 242 = 256
        RegisterRequest request = new RegisterRequest("John Doe", "test@example.com", overMaxPassword);

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty(), "Password over max length should fail validation");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("password")));
    }

    @Test
    @DisplayName("Password at min length (8 characters) should be valid if strong")
    void testPasswordMinLength() {
        RegisterRequest request = new RegisterRequest("John Doe", "test@example.com", "Strong1!");

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        assertTrue(violations.isEmpty(), "Strong password at min length should be valid");
    }

    @Test
    @DisplayName("Password under min length (7 characters) should fail validation")
    void testPasswordUnderMinLength() {
        RegisterRequest request = new RegisterRequest("John Doe", "test@example.com", "Short1!");

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty(), "Password under min length should fail validation");
    }

    @Test
    @DisplayName("Blank password should fail validation")
    void testBlankPassword() {
        RegisterRequest request = new RegisterRequest("John Doe", "test@example.com", "");

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty(), "Blank password should fail validation");
    }

    // Email validation tests
    @Test
    @DisplayName("Debug Email Validation")
    void debugEmailValidation() {
        String email = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa@example.subdomain.withaveryverylongdomainnamethatkeepsgoingandgoingandstillfitsunderthe255characterlimitasrequiredbythestandard.com";

        RegisterRequest request = new RegisterRequest("John Doe", email, "StrongPass123!");

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        System.out.println("Number of violations: " + violations.size());

        for (ConstraintViolation<RegisterRequest> violation : violations) {
            System.out.println("Field: " + violation.getPropertyPath());
            System.out.println("Message: " + violation.getMessage());
            System.out.println("Invalid value: " + violation.getInvalidValue());
            System.out.println("---");
        }
    }

    @Test
    @DisplayName("Email over max length (256 characters) should fail validation")
    void testEmailOverMaxLength() {
        // Create an email that's 256 characters (over the limit)
        // Using "example.com" (11 chars) + "@" (1 char) = 12 chars for domain part
        // So localpart should be 256 - 12 = 244 characters
        String localPart = "a".repeat(244);
        String longEmail = localPart + "@example.com"; // 244 + 12 = 256 characters

        RegisterRequest request = new RegisterRequest("John Doe", longEmail, "StrongPass123!");

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty(), "Email over max length should fail validation");
        assertTrue(violations.stream().anyMatch(v ->
                        v.getPropertyPath().toString().equals("email") &&
                                v.getMessage().contains("255 characters")),
                "Should have a size constraint violation for email field");
    }

    @Test
    @DisplayName("Invalid email format should fail validation")
    void testInvalidEmailFormat() {
        RegisterRequest request = new RegisterRequest("John Doe", "invalid-email", "StrongPass123!");

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty(), "Invalid email format should fail validation");
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
    }

    @Test
    @DisplayName("Blank email should fail validation")
    void testBlankEmail() {
        RegisterRequest request = new RegisterRequest("John Doe", "", "StrongPass123!");

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty(), "Blank email should fail validation");
    }

    @Test
    @DisplayName("Multiple validation errors should all be captured")
    void testMultipleValidationErrors() {
        RegisterRequest request = new RegisterRequest("", "invalid-email", "weak");

        Set<ConstraintViolation<RegisterRequest>> violations = validator.validate(request);

        assertFalse(violations.isEmpty(), "Multiple validation errors should be captured");
        assertTrue(violations.size() >= 3, "Should have at least 3 validation errors");

        // Check that we have violations for all three fields
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("username")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("email")));
        assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString().equals("password")));
    }
}