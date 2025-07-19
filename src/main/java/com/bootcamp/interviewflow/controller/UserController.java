package com.bootcamp.interviewflow.controller;

import com.bootcamp.interviewflow.dto.ApiResponse;
import com.bootcamp.interviewflow.dto.LoginRequest;
import com.bootcamp.interviewflow.dto.RegisterRequest;
import com.bootcamp.interviewflow.dto.UserResponse;
import com.bootcamp.interviewflow.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Tag(name = "Users", description = "User related operations")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Register a new user")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201", description = "User registered successfully",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400", description = "Invalid registration data", content = @Content)
    })
    @PostMapping("/auth/register")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        UserResponse userResponse = userService.register(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse(true, "User registered successfully", userResponse));
    }

    @Operation(summary = "Login an existing user")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "Login successful",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400", description = "Invalid input data - validation errors",
                    content = @Content(schema = @Schema(implementation = com.bootcamp.interviewflow.dto.ValidationErrorResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401", description = "Invalid credentials", content = @Content)
    })
    @PostMapping("/auth/login")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        UserResponse userResponse = userService.login(loginRequest);
        return ResponseEntity.ok(new ApiResponse(true, "Login successful", userResponse));
    }

    @Operation(summary = "User dashboard")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200", description = "Dashboard",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "401", description = "Unauthorized",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class)))
    })
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse> dashboard() {
        // This endpoint requires Authorization header: Basic base64(email:password)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();

        return ResponseEntity.ok(new ApiResponse(true,
                "Welcome to dashboard, " + currentUserEmail,
                null));
    }
}