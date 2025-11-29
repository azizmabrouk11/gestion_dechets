package com.nourproject.backend.exceptions;
public class GlobalException extends RuntimeException{
    public GlobalException(String message) {
        super(message);
    }
}