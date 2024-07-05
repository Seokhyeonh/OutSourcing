package com.sparta.deventer.exception;

import com.sparta.deventer.enums.MismatchStatusEntity;

public class MismatchStatusException extends RuntimeException {

    public MismatchStatusException(MismatchStatusEntity mismatchStatusEntity) {
        super(mismatchStatusEntity.getMessage());
    }
}
