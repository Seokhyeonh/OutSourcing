package com.sparta.deventer.dto;

import com.sparta.deventer.entity.User;
import com.sparta.deventer.enums.UserRole;
import com.sparta.deventer.enums.UserStatus;
import lombok.Getter;

@Getter
public class UserResponseDto {

    private final Long userId;
    private final String username;
    private final String nickname;
    private final String email;
    private final UserRole role;
    private final UserStatus status;

    public UserResponseDto(User user) {
        this.userId = user.getId();
        this.username = user.getUsername();
        this.nickname = user.getNickname();
        this.email = user.getEmail();
        this.role = user.getRole();
        this.status = user.getStatus();
    }
}