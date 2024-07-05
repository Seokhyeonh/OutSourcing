package com.sparta.deventer.dto;

import lombok.Getter;

@Getter
public class GitHubUserDto {

    private String login;
    private Long id;
    private String avatar_url;
    private String url;
    private String email;
    private String name;
}
