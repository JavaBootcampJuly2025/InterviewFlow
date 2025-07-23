package com.bootcamp.interviewflow.service;

import com.bootcamp.interviewflow.dto.ChangePasswordRequest;
import com.bootcamp.interviewflow.dto.LoginRequest;
import com.bootcamp.interviewflow.dto.RegisterRequest;
import com.bootcamp.interviewflow.dto.UserRequest;
import com.bootcamp.interviewflow.dto.UserResponse;

public interface UserService {

    UserResponse register(RegisterRequest registerRequest);

    UserResponse login(LoginRequest loginRequest);

    UserResponse getCurrentUserProfile(Long authenticatedUserId);

    UserResponse updateCurrentUserProfile(Long authenticatedUserId, UserRequest request);

    void deleteCurrentUser(Long authenticatedUserId);

    void changePassword(Long authenticatedUserId, ChangePasswordRequest request);
}
