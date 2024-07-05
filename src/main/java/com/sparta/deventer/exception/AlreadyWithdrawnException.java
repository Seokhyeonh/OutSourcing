package com.sparta.deventer.exception;

public class AlreadyWithdrawnException extends RuntimeException {

    public AlreadyWithdrawnException(String message) {
        super(message);
    }
}