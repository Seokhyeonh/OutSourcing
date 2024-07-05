package com.sparta.deventer.enums;

import lombok.Getter;

@Getter
public enum UserStatus {
    ACTIVE("ACTIVE"),
    BLOCKED("BLOCKED"),
    DELETED("DELETED");

    private final String status;

    UserStatus(String status) {
        this.status = status;
    }
}