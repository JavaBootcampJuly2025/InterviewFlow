package com.bootcamp.interviewflow.service;

import com.bootcamp.interviewflow.dto.LoginRequest;
import com.bootcamp.interviewflow.dto.RegisterRequest;
import com.bootcamp.interviewflow.dto.UserResponse;

public interface UserService {

    UserResponse register(RegisterRequest registerRequest);

    UserResponse login(LoginRequest loginRequest);
}
