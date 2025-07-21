package com.bootcamp.interviewflow.service;

import com.bootcamp.interviewflow.dto.LoginRequest;
import com.bootcamp.interviewflow.dto.RegisterRequest;
import com.bootcamp.interviewflow.dto.UserRequest;
import com.bootcamp.interviewflow.dto.UserResponse;

public interface UserService {

    UserResponse register(RegisterRequest registerRequest);

    UserResponse login(LoginRequest loginRequest);

    UserResponse getUserProfile(Long requestedId, Long authenticatedUserId);

    UserResponse updateUserProfile(Long userId, Long authenticatedUserId, UserRequest request);

    void deleteUser(Long userId, Long authenticatedUserId);
}
