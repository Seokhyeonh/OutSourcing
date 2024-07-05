package com.sparta.deventer.dto;

import lombok.Getter;

@Getter
public class GitHubTokenResponseDto {

    private String access_token;
    private String scope;
    private String token_type;
}