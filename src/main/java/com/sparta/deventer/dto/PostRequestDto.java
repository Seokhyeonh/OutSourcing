package com.sparta.deventer.dto;

import lombok.Getter;
@Getter
public class PostRequestDto {

    private final String title;
    private final String content;
    private final String categoryTopic;

    public PostRequestDto(String title, String content, String categoryTopic) {
        this.title = title;
        this.content = content;
        this.categoryTopic = categoryTopic;
    }



}
