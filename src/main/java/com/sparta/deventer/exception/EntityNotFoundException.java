package com.sparta.deventer.exception;

import com.sparta.deventer.enums.NotFoundEntity;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(NotFoundEntity notFoundEntity) {
        super(notFoundEntity.getMessage());
    }
}