package com.sparta.deventer.exception;

public class InvalidAdminCodeException extends RuntimeException {

    public InvalidAdminCodeException(String message) {
        super(message);
    }
}