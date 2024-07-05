package com.sparta.deventer.entity;

import java.util.Objects;
import lombok.Getter;

@Getter
public enum ContentEnumType {
    POST("post"), COMMENT("comment");

    private final String type;

    ContentEnumType(String type) {
        this.type = type;
    }

    public static ContentEnumType getByType(String type) {
        if (Objects.equals(type, POST.type)) {
            return POST;
        } else if (Objects.equals(type, COMMENT.type)) {
            return COMMENT;
        } else {
            throw new IllegalArgumentException("좋아요 타입이 일치 하지 않습니다.");
        }
    }
}
