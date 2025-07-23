package com.bootcamp.interviewflow.service;

import com.bootcamp.interviewflow.dto.LoginRequest;
import com.bootcamp.interviewflow.dto.RegisterRequest;
import com.bootcamp.interviewflow.dto.UserRequest;
import com.bootcamp.interviewflow.dto.UserResponse;
import com.bootcamp.interviewflow.exception.EmailAlreadyExistsException;
import com.bootcamp.interviewflow.model.User;
import com.bootcamp.interviewflow.repository.UserRepository;
import com.bootcamp.interviewflow.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Implementation Tests")
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UserServiceImpl userService;

    private UserRequest validUserRequest;
    private RegisterRequest validRegisterRequest;
    private LoginRequest validLoginRequest;
    private User mockUser;
    private String mockToken;

    @BeforeEach
    void setUp() {
        validRegisterRequest = new RegisterRequest();
        validRegisterRequest.setUsername("John Doe");
        validRegisterRequest.setEmail("john.doe@example.com");
        validRegisterRequest.setPassword("StrongPass123!");

        // Setup valid login request
        validLoginRequest = new LoginRequest();
        validLoginRequest.setEmail("john.doe@example.com");
        validLoginRequest.setPassword("StrongPass123!");

        // Setup mock user
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setUsername("John Doe");
        mockUser.setEmail("john.doe@example.com");
        mockUser.setPassword("encodedPassword");

        validUserRequest = new UserRequest();
        validUserRequest.setUsername("Alice");
        validUserRequest.setEmail("alice@example.com");
        mockUser.setCreatedAt(LocalDateTime.now());

        // Setup mock token
        mockToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJqb2huLmRvZUBleGFtcGxlLmNvbSIsInVzZXJJZCI6MX0.test";
    }

    @Test
    @DisplayName("Should successfully register a new user")
    void shouldSuccessfullyRegisterNewUser() {
        when(userRepository.findByEmail(validRegisterRequest.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(validRegisterRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        UserResponse result = userService.register(validRegisterRequest);

        assertNotNull(result);
        assertEquals(mockUser.getId(), result.getId());
        assertEquals(mockUser.getUsername(), result.getUserName());
        assertEquals(mockUser.getEmail(), result.getEmail());
        assertEquals(mockUser.getCreatedAt(), result.getCreatedAt());

        verify(userRepository).findByEmail(validRegisterRequest.getEmail());
        verify(passwordEncoder).encode(validRegisterRequest.getPassword());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw EmailAlreadyExistsException when email already exists")
    void shouldThrowEmailAlreadyExistsExceptionWhenEmailExists() {
        when(userRepository.findByEmail(validRegisterRequest.getEmail())).thenReturn(Optional.of(mockUser));

        EmailAlreadyExistsException exception = assertThrows(
                EmailAlreadyExistsException.class,
                () -> userService.register(validRegisterRequest)
        );

        assertEquals("Email already exists: " + validRegisterRequest.getEmail(), exception.getMessage());

        verify(userRepository).findByEmail(validRegisterRequest.getEmail());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should properly encode password during registration")
    void shouldProperlyEncodePasswordDuringRegistration() {
        when(userRepository.findByEmail(validRegisterRequest.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(validRegisterRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User user = invocation.getArgument(0);
            user.setId(1L);
            user.setCreatedAt(LocalDateTime.now());
            return user;
        });

        userService.register(validRegisterRequest);

        verify(passwordEncoder).encode(validRegisterRequest.getPassword());
        verify(userRepository).save(argThat(user ->
                user.getPassword().equals("encodedPassword") &&
                        user.getUsername().equals(validRegisterRequest.getUsername()) &&
                        user.getEmail().equals(validRegisterRequest.getEmail())
        ));
    }

    @Test
    @DisplayName("Should successfully login with valid credentials and return token")
    void shouldSuccessfullyLoginWithValidCredentialsAndReturnToken() {
        when(userRepository.findByEmail(validLoginRequest.getEmail())).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(validLoginRequest.getPassword(), mockUser.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(mockUser.getEmail(), mockUser.getId())).thenReturn(mockToken);

        UserResponse result = userService.login(validLoginRequest);

        assertNotNull(result);
        assertEquals(mockUser.getId(), result.getId());
        assertEquals(mockUser.getUsername(), result.getUserName());
        assertEquals(mockUser.getEmail(), result.getEmail());
        assertEquals(mockToken, result.getAccessToken());

        verify(userRepository).findByEmail(validLoginRequest.getEmail());
        verify(passwordEncoder).matches(validLoginRequest.getPassword(), mockUser.getPassword());
        verify(jwtUtil).generateToken(mockUser.getEmail(), mockUser.getId());
    }

    @Test
    @DisplayName("Should throw BadCredentialsException when user not found")
    void shouldThrowBadCredentialsExceptionWhenUserNotFound() {
        when(userRepository.findByEmail(validLoginRequest.getEmail())).thenReturn(Optional.empty());

        BadCredentialsException exception = assertThrows(
                BadCredentialsException.class,
                () -> userService.login(validLoginRequest)
        );

        assertEquals("Invalid email or password", exception.getMessage());

        verify(userRepository).findByEmail(validLoginRequest.getEmail());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtUtil, never()).generateToken(anyString(), anyLong());
    }

    @Test
    @DisplayName("Should throw BadCredentialsException when password is incorrect")
    void shouldThrowBadCredentialsExceptionWhenPasswordIncorrect() {
        when(userRepository.findByEmail(validLoginRequest.getEmail())).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(validLoginRequest.getPassword(), mockUser.getPassword())).thenReturn(false);

        BadCredentialsException exception = assertThrows(
                BadCredentialsException.class,
                () -> userService.login(validLoginRequest)
        );

        assertEquals("Invalid email or password", exception.getMessage());

        verify(userRepository).findByEmail(validLoginRequest.getEmail());
        verify(passwordEncoder).matches(validLoginRequest.getPassword(), mockUser.getPassword());
        verify(jwtUtil, never()).generateToken(anyString(), anyLong());
    }

    @Test
    @DisplayName("Should handle null email in register request")
    void shouldHandleNullEmailInRegisterRequest() {
        validRegisterRequest.setEmail(null);
        when(userRepository.findByEmail(null)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(validRegisterRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        UserResponse result = userService.register(validRegisterRequest);

        assertNotNull(result);
        verify(userRepository).findByEmail(null);
    }

    @Test
    @DisplayName("Should handle null email in login request")
    void shouldHandleNullEmailInLoginRequest() {
        validLoginRequest.setEmail(null);
        when(userRepository.findByEmail(null)).thenReturn(Optional.empty());

        BadCredentialsException exception = assertThrows(
                BadCredentialsException.class,
                () -> userService.login(validLoginRequest)
        );

        assertEquals("Invalid email or password", exception.getMessage());
        verify(userRepository).findByEmail(null);
        verify(jwtUtil, never()).generateToken(anyString(), anyLong());
    }

    @Test
    @DisplayName("Should verify transaction annotation on register method")
    void shouldVerifyTransactionAnnotationOnRegisterMethod() throws NoSuchMethodException {
        var registerMethod = UserServiceImpl.class.getDeclaredMethod("register", RegisterRequest.class);

        assertTrue(registerMethod.isAnnotationPresent(org.springframework.transaction.annotation.Transactional.class));
    }

    @Test
    @DisplayName("Should handle empty string email in register request")
    void shouldHandleEmptyStringEmailInRegisterRequest() {
        validRegisterRequest.setEmail("");
        when(userRepository.findByEmail("")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(validRegisterRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        UserResponse result = userService.register(validRegisterRequest);

        assertNotNull(result);
        verify(userRepository).findByEmail("");
    }

    @Test
    @DisplayName("Should handle empty string email in login request")
    void shouldHandleEmptyStringEmailInLoginRequest() {
        validLoginRequest.setEmail("");
        when(userRepository.findByEmail("")).thenReturn(Optional.empty());

        BadCredentialsException exception = assertThrows(
                BadCredentialsException.class,
                () -> userService.login(validLoginRequest)
        );

        assertEquals("Invalid email or password", exception.getMessage());
        verify(userRepository).findByEmail("");
        verify(jwtUtil, never()).generateToken(anyString(), anyLong());
    }

    @Test
    @DisplayName("Should create user with correct field mappings during registration")
    void shouldCreateUserWithCorrectFieldMappingsDuringRegistration() {
        when(userRepository.findByEmail(validRegisterRequest.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(validRegisterRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setId(1L);
            savedUser.setCreatedAt(LocalDateTime.now());
            return savedUser;
        });

        userService.register(validRegisterRequest);

        verify(userRepository).save(argThat(user -> {
            boolean usernameMatches = validRegisterRequest.getUsername().equals(user.getUsername());
            boolean emailMatches = validRegisterRequest.getEmail().equals(user.getEmail());
            boolean passwordEncoded = "encodedPassword".equals(user.getPassword());
            return usernameMatches && emailMatches && passwordEncoded;
        }));
    }

    @Test
    @DisplayName("Should return UserResponse with all fields populated during registration")
    void shouldReturnUserResponseWithAllFieldsPopulatedDuringRegistration() {
        LocalDateTime fixedTime = LocalDateTime.of(2023, 1, 1, 12, 0, 0);
        mockUser.setCreatedAt(fixedTime);

        when(userRepository.findByEmail(validRegisterRequest.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(validRegisterRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        UserResponse result = userService.register(validRegisterRequest);

        assertAll(
                () -> assertEquals(1L, result.getId()),
                () -> assertEquals("John Doe", result.getUserName()),
                () -> assertEquals("john.doe@example.com", result.getEmail()),
                () -> assertEquals(fixedTime, result.getCreatedAt())
        );
    }

    @Test
    @DisplayName("Should return UserResponse with all fields populated during login including token")
    void shouldReturnUserResponseWithAllFieldsPopulatedDuringLoginIncludingToken() {
        when(userRepository.findByEmail(validLoginRequest.getEmail())).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(validLoginRequest.getPassword(), mockUser.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(mockUser.getEmail(), mockUser.getId())).thenReturn(mockToken);

        UserResponse result = userService.login(validLoginRequest);

        assertAll(
                () -> assertEquals(1L, result.getId()),
                () -> assertEquals("John Doe", result.getUserName()),
                () -> assertEquals("john.doe@example.com", result.getEmail()),
                () -> assertEquals(mockToken, result.getAccessToken())
        );
    }

    @Test
    @DisplayName("Should handle repository exception during registration")
    void shouldHandleRepositoryExceptionDuringRegistration() {
        when(userRepository.findByEmail(validRegisterRequest.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(validRegisterRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenThrow(new RuntimeException("Database connection failed"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> userService.register(validRegisterRequest)
        );

        assertEquals("Database connection failed", exception.getMessage());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should handle repository exception during email check in registration")
    void shouldHandleRepositoryExceptionDuringEmailCheckInRegistration() {
        when(userRepository.findByEmail(validRegisterRequest.getEmail()))
                .thenThrow(new RuntimeException("Database query failed"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> userService.register(validRegisterRequest)
        );

        assertEquals("Database query failed", exception.getMessage());
        verify(userRepository).findByEmail(validRegisterRequest.getEmail());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should handle repository exception during login")
    void shouldHandleRepositoryExceptionDuringLogin() {
        when(userRepository.findByEmail(validLoginRequest.getEmail()))
                .thenThrow(new RuntimeException("Database connection lost"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> userService.login(validLoginRequest)
        );

        assertEquals("Database connection lost", exception.getMessage());
        verify(userRepository).findByEmail(validLoginRequest.getEmail());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(jwtUtil, never()).generateToken(anyString(), anyLong());
    }

    @Test
    @DisplayName("Should handle password encoder exception during registration")
    void shouldHandlePasswordEncoderExceptionDuringRegistration() {
        when(userRepository.findByEmail(validRegisterRequest.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(validRegisterRequest.getPassword()))
                .thenThrow(new RuntimeException("Encoding algorithm not available"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> userService.register(validRegisterRequest)
        );

        assertEquals("Encoding algorithm not available", exception.getMessage());
        verify(passwordEncoder).encode(validRegisterRequest.getPassword());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should handle password encoder exception during login")
    void shouldHandlePasswordEncoderExceptionDuringLogin() {
        when(userRepository.findByEmail(validLoginRequest.getEmail())).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(validLoginRequest.getPassword(), mockUser.getPassword()))
                .thenThrow(new RuntimeException("Password matching failed"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> userService.login(validLoginRequest)
        );

        assertEquals("Password matching failed", exception.getMessage());
        verify(passwordEncoder).matches(validLoginRequest.getPassword(), mockUser.getPassword());
        verify(jwtUtil, never()).generateToken(anyString(), anyLong());
    }

    @Test
    @DisplayName("Should handle JWT token generation exception during login")
    void shouldHandleJwtTokenGenerationExceptionDuringLogin() {
        when(userRepository.findByEmail(validLoginRequest.getEmail())).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(validLoginRequest.getPassword(), mockUser.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(mockUser.getEmail(), mockUser.getId()))
                .thenThrow(new RuntimeException("Token generation failed"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> userService.login(validLoginRequest)
        );

        assertEquals("Token generation failed", exception.getMessage());
        verify(jwtUtil).generateToken(mockUser.getEmail(), mockUser.getId());
    }

    @Test
    @DisplayName("Should handle case-sensitive email during registration")
    void shouldHandleCaseSensitiveEmailDuringRegistration() {
        String upperCaseEmail = "JOHN.DOE@EXAMPLE.COM";
        validRegisterRequest.setEmail(upperCaseEmail);

        when(userRepository.findByEmail(upperCaseEmail)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(validRegisterRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        UserResponse result = userService.register(validRegisterRequest);

        assertNotNull(result);
        verify(userRepository).findByEmail(upperCaseEmail);
        verify(userRepository).save(argThat(user -> upperCaseEmail.equals(user.getEmail())));
    }

    @Test
    @DisplayName("Should handle case-sensitive email during login")
    void shouldHandleCaseSensitiveEmailDuringLogin() {
        String upperCaseEmail = "JOHN.DOE@EXAMPLE.COM";
        validLoginRequest.setEmail(upperCaseEmail);
        mockUser.setEmail(upperCaseEmail);

        when(userRepository.findByEmail(upperCaseEmail)).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(validLoginRequest.getPassword(), mockUser.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(upperCaseEmail, mockUser.getId())).thenReturn(mockToken);

        UserResponse result = userService.login(validLoginRequest);

        assertNotNull(result);
        assertEquals(upperCaseEmail, result.getEmail());
        assertEquals(mockToken, result.getAccessToken());
        verify(userRepository).findByEmail(upperCaseEmail);
        verify(jwtUtil).generateToken(upperCaseEmail, mockUser.getId());
    }

    @Test
    @DisplayName("Should handle special characters in username during registration")
    void shouldHandleSpecialCharactersInUsernameDuringRegistration() {
        validRegisterRequest.setUsername("José María O'Connor-Smith");

        when(userRepository.findByEmail(validRegisterRequest.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(validRegisterRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        UserResponse result = userService.register(validRegisterRequest);

        assertNotNull(result);
        verify(userRepository).save(argThat(user ->
                "José María O'Connor-Smith".equals(user.getUsername())
        ));
    }

    @Test
    @DisplayName("Should handle very long email during registration")
    void shouldHandleVeryLongEmailDuringRegistration() {
        String longEmail = "a".repeat(240) + "@example.com"; // 251 characters total
        validRegisterRequest.setEmail(longEmail);

        when(userRepository.findByEmail(longEmail)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(validRegisterRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        UserResponse result = userService.register(validRegisterRequest);

        assertNotNull(result);
        verify(userRepository).findByEmail(longEmail);
    }

    @Test
    @DisplayName("Should handle password with special characters during login")
    void shouldHandlePasswordWithSpecialCharactersDuringLogin() {
        String complexPassword = "P@$w0rd!#$%^&*()_+-=[]{}|;:,.<>?";
        validLoginRequest.setPassword(complexPassword);

        when(userRepository.findByEmail(validLoginRequest.getEmail())).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(complexPassword, mockUser.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(mockUser.getEmail(), mockUser.getId())).thenReturn(mockToken);

        UserResponse result = userService.login(validLoginRequest);

        assertNotNull(result);
        assertEquals(mockToken, result.getAccessToken());
        verify(passwordEncoder).matches(complexPassword, mockUser.getPassword());
    }

    @Test
    @DisplayName("Should handle user with null username")
    void shouldHandleUserWithNullUsername() {
        mockUser.setUsername(null);

        when(userRepository.findByEmail(validLoginRequest.getEmail())).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(validLoginRequest.getPassword(), mockUser.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(mockUser.getEmail(), mockUser.getId())).thenReturn(mockToken);

        UserResponse result = userService.login(validLoginRequest);

        assertNotNull(result);
        assertNull(result.getUserName());
        assertEquals(mockToken, result.getAccessToken());
    }

    @Test
    @DisplayName("Should verify exact method call order during successful registration")
    void shouldVerifyExactMethodCallOrderDuringSuccessfulRegistration() {
        when(userRepository.findByEmail(validRegisterRequest.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(validRegisterRequest.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(mockUser);

        userService.register(validRegisterRequest);

        var inOrder = inOrder(userRepository, passwordEncoder);
        inOrder.verify(userRepository).findByEmail(validRegisterRequest.getEmail());
        inOrder.verify(passwordEncoder).encode(validRegisterRequest.getPassword());
        inOrder.verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should verify exact method call order during successful login")
    void shouldVerifyExactMethodCallOrderDuringSuccessfulLogin() {
        when(userRepository.findByEmail(validLoginRequest.getEmail())).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(validLoginRequest.getPassword(), mockUser.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(mockUser.getEmail(), mockUser.getId())).thenReturn(mockToken);

        userService.login(validLoginRequest);

        var inOrder = inOrder(userRepository, passwordEncoder, jwtUtil);
        inOrder.verify(userRepository).findByEmail(validLoginRequest.getEmail());
        inOrder.verify(passwordEncoder).matches(validLoginRequest.getPassword(), mockUser.getPassword());
        inOrder.verify(jwtUtil).generateToken(mockUser.getEmail(), mockUser.getId());
    }

    @Test
    @DisplayName("Should verify JwtUtil is called with correct parameters")
    void shouldVerifyJwtUtilIsCalledWithCorrectParameters() {
        when(userRepository.findByEmail(validLoginRequest.getEmail())).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(validLoginRequest.getPassword(), mockUser.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(mockUser.getEmail(), mockUser.getId())).thenReturn(mockToken);

        userService.login(validLoginRequest);

        verify(jwtUtil).generateToken(
                eq(mockUser.getEmail()),
                eq(mockUser.getId())
        );
    }

    @Test
    @DisplayName("Should return profile for current user when exists")
    void shouldReturnProfileForCurrentUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        UserResponse response = userService.getCurrentUserProfile(1L);

        assertNotNull(response);
        assertEquals(mockUser.getId(), response.getId());
        assertEquals(mockUser.getUsername(), response.getUserName());
        assertEquals(mockUser.getEmail(), response.getEmail());
        assertEquals(mockUser.getCreatedAt(), response.getCreatedAt());

        verify(userRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException if profile does not exist")
    void getCurrentUserProfile_nonExistingUser_throwsEntityNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> userService.getCurrentUserProfile(1L)
        );
        assertEquals("User not found with ID: 1", exception.getMessage());

        verify(userRepository).findById(1L);
    }

    @Test
    @DisplayName("Should successfully update profile for current user")
    void shouldUpdateProfileForCurrentUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(userRepository.findByEmail(validUserRequest.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(ArgumentMatchers.any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse result = userService.updateCurrentUserProfile(1L, validUserRequest);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Alice", result.getUserName());
        assertEquals("alice@example.com", result.getEmail());

        verify(userRepository).findById(1L);
        verify(userRepository).findByEmail("alice@example.com");
        verify(userRepository).save(argThat(user ->
                "Alice".equals(user.getUsername()) &&
                        "alice@example.com".equals(user.getEmail())
        ));
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when updating non-existent profile")
    void updateCurrentUserProfile_nonExistingUser_throwsEntityNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> userService.updateCurrentUserProfile(1L, validUserRequest)
        );
        assertEquals("User not found with ID: 1", exception.getMessage());

        verify(userRepository).findById(1L);
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw EmailAlreadyExistsException when new email already in use")
    void updateCurrentUserProfile_emailConflict_throwsEmailAlreadyExistsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(userRepository.findByEmail(validUserRequest.getEmail())).thenReturn(Optional.of(new User()));

        EmailAlreadyExistsException exception = assertThrows(
                EmailAlreadyExistsException.class,
                () -> userService.updateCurrentUserProfile(1L, validUserRequest)
        );
        assertEquals("Email already in use: alice@example.com", exception.getMessage());

        verify(userRepository).findByEmail("alice@example.com");
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should delete current user when exists")
    void shouldDeleteCurrentUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        assertDoesNotThrow(() -> userService.deleteCurrentUser(1L));

        verify(userRepository).findById(1L);
        verify(userRepository).delete(mockUser);
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when deleting non-existent user")
    void deleteCurrentUser_nonExistingUser_throwsEntityNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> userService.deleteCurrentUser(1L)
        );
        assertEquals("User not found with ID: 1", exception.getMessage());

        verify(userRepository).findById(1L);
        verify(userRepository, never()).delete(any());
    }
}