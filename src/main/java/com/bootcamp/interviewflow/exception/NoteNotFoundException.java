package com.bootcamp.interviewflow.exception;

public class NoteNotFoundException extends RuntimeException {
    public NoteNotFoundException(Long id) {
        super("Note with Id " + id + " not found");
    }
}
