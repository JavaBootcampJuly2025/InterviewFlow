package com.bootcamp.interviewflow.dto;

import java.time.LocalDateTime;

public class UserResponse {

    private Long id;
    private String userName;
    private String email;
    private LocalDateTime createdAt;

    public UserResponse() {}

    public UserResponse(Long id, String userName, String email, LocalDateTime createdAt) {
        this.id = id;
        this.userName = userName;
        this.email = email;
        this.createdAt = createdAt;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}