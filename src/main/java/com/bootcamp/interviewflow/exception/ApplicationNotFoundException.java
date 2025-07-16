package com.bootcamp.interviewflow.exception;

public class ApplicationNotFoundException extends RuntimeException {
    public ApplicationNotFoundException(Long id) {
        super("Application with Id " + id + " not found");
    }
}
