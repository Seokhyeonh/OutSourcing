package com.sparta.deventer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequestDto {

    @NotBlank(message = "아이디는 공백일 수 없습니다.")
    @Size(min = 4, max = 10, message = "아이디는 4자이상 10자 이하여야합니다.")
    @Pattern(regexp = "^[a-z0-9!@#$%^&*()]+$",
            message = "아이디는 알파벳 소문자, 숫자, 특수문자만 사용가능합니다.")
    private String username;

    @NotBlank(message = "패스워드는 공백일 수 없습니다.")
    @Size(min = 8, max = 15, message = "비밀번호는 8자이상 15자 이하여야합니다.")
    @Pattern(regexp = "^[a-zA-Z0-9!@#$%^&*()]+$",
            message = "비밀번호는 알파벳 대소문자, 숫자, 특수문자만 사용가능합니다.")
    private String password;
}