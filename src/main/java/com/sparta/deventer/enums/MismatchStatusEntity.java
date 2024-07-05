package com.sparta.deventer.enums;

import lombok.Getter;

@Getter
public enum MismatchStatusEntity {
    MISMATCH_STATUS_BLOCKED_USER("블록 처리 당한 유저 입니다."),
    MISMATCH_STATUS_DELETED_USER("탈퇴 처리 당한 쥬저 입니다."),
    MISMATCH_USER("본인이 아닙니다."),
    SELF_USER("본인으로 확인되어 작업이 불가능 합니다.");

    private final String message;

    MismatchStatusEntity(String message) {
        this.message = message;
    }
}
