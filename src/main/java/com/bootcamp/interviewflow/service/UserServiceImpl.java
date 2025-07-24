package com.bootcamp.interviewflow.service;

import com.bootcamp.interviewflow.dto.*;
import com.bootcamp.interviewflow.exception.EmailAlreadyExistsException;
import com.bootcamp.interviewflow.model.User;
import com.bootcamp.interviewflow.repository.UserRepository;
import com.bootcamp.interviewflow.security.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    @Transactional
    public UserResponse register(RegisterRequest registerRequest) {
        logger.info("Attempting to register user with email: {}", registerRequest.getEmail());

        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email already exists: " + registerRequest.getEmail());
        }

        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        User savedUser = userRepository.save(user);
        logger.info("User registered successfully with ID: {}", savedUser.getId());

        return convertToResponse(savedUser);
    }

    @Override
    public UserResponse login(LoginRequest loginRequest) {
        logger.info("Attempting to login user with email: {}", loginRequest.getEmail());

        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getId());

        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                token
        );
    }

    @Override
    public UserResponse getCurrentUserProfile(Long authenticatedUserId) {
        logger.info("Fetching profile for userId: {}", authenticatedUserId);
        User user = findUserById(authenticatedUserId);
        return convertToResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateCurrentUserProfile(Long authenticatedUserId, UserRequest request) {
        logger.info("Updating profile for userId: {}", authenticatedUserId);
        User user = findUserById(authenticatedUserId);

        if (!user.getEmail().equals(request.getEmail())
                && userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email already in use: " + request.getEmail());
        }

        logger.debug("New username: {}, New email: {}", request.getUsername(), request.getEmail());
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());

        User updated = userRepository.save(user);
        return convertToResponse(updated);
    }

    @Override
    @Transactional
    public void deleteCurrentUser(Long authenticatedUserId) {
        logger.info("Deleting user with ID: {}", authenticatedUserId);
        User user = findUserById(authenticatedUserId);
        userRepository.delete(user);
    }

    @Override
    @Transactional
    public void changePassword(Long authenticatedUserId, ChangePasswordRequest request) {
        logger.info("Changing password for userId: {}", authenticatedUserId);
        User user = findUserById(authenticatedUserId);

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BadCredentialsException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        logger.info("Password changed successfully for userId: {}", authenticatedUserId);
    }

    private UserResponse convertToResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getCreatedAt()
        );
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));
    }
}