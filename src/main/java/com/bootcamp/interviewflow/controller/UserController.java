package com.bootcamp.interviewflow.controller;

import com.bootcamp.interviewflow.dto.ApiResponse;
import com.bootcamp.interviewflow.dto.LoginDto;
import com.bootcamp.interviewflow.dto.RegisterDto;
import com.bootcamp.interviewflow.dto.UserResponseDto;
import com.bootcamp.interviewflow.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody RegisterDto registerDto) {
        try {
            UserResponseDto userResponse = userService.register(registerDto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse(true, "User registered successfully", userResponse));
        } catch (Exception e) {
            logger.error("Registration failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody LoginDto loginDto) {
        try {
            UserResponseDto userResponse = userService.login(loginDto);
            return ResponseEntity.ok(new ApiResponse(true, "Login successful", userResponse));
        } catch (Exception e) {
            logger.error("Login failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse> dashboard() {
        // This is a placeholder for dashboard endpoint
        return ResponseEntity.ok(new ApiResponse(true, "Welcome to dashboard", null));
    }
}