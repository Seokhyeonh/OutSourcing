package com.sparta.deventer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChangeNicknameRequestDto {

    @NotBlank(message = "새로운 닉네임을 입력해야 합니다.")
    @Pattern(regexp = "^[a-z0-9_]+$", message = "닉네임은 소문자, 숫자, 밑줄(_)만 사용할 수 있습니다.")
    private String newNickname;
}