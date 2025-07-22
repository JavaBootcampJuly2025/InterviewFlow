package com.bootcamp.interviewflow.exception;

public class FileNotFoundOrNoAccessException extends RuntimeException {
    public FileNotFoundOrNoAccessException(String message) {
        super(message);
    }
}
