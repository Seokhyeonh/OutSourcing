package com.sparta.deventer.exception;

import java.util.List;
import lombok.Getter;

@Getter
public class UserInfoDuplicateException extends RuntimeException {

    private List<String> errorMessages;

    public UserInfoDuplicateException(List<String> errorMessageList) {
        this.errorMessages = errorMessageList;
    }
}
