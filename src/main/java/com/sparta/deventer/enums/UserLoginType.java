package com.sparta.deventer.enums;

public enum UserLoginType {

    DEFAULT("Default"),
    GITHUB("Github"),
    GOOGLE("Google"),
    NAVER("Naver"),
    KAKAO("Kakao");

    private final String loginType;

    UserLoginType(String loginType) {
        this.loginType = loginType;
    }

}
