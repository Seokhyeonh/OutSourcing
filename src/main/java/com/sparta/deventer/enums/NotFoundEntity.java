package com.sparta.deventer.enums;

import lombok.Getter;

@Getter
public enum NotFoundEntity {
    CATEGORY_NOT_FOUND("카테고리를 찾을 수 없습니다."),
    COMMENT_NOT_FOUND("댓글을 찾을 수 없습니다."),
    POST_NOT_FOUND("게시물을 찾을 수 없습니다."),
    USER_NOT_FOUND("사용자를 찾을 수 없습니다.");

    private final String message;

    NotFoundEntity(String message) {
        this.message = message;
    }
}