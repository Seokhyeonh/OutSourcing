package com.sparta.deventer.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class CategoryRequestDto {

    @NotNull(message = "카테고리 주제는 필수 입력값입니다.")
    @Size(min = 3, max = 20, message = "카테고리 주제는 3글자 이상 20글자 이하여야 합니다.")
    @Pattern(regexp = "^[a-zA-Z가-힣]+$",
            message = "카테고리 주제는 한글, 영어 대소문자로만 입력해야 합니다.")
    private String topic;
}
