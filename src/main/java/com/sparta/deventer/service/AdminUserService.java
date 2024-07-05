package com.sparta.deventer.service;

import com.sparta.deventer.dto.ChangeNicknameRequestDto;
import com.sparta.deventer.dto.UserResponseDto;
import com.sparta.deventer.entity.User;
import com.sparta.deventer.enums.NotFoundEntity;
import com.sparta.deventer.enums.UserRole;
import com.sparta.deventer.enums.UserStatus;
import com.sparta.deventer.exception.EntityNotFoundException;
import com.sparta.deventer.exception.InvalidOperationException;
import com.sparta.deventer.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final UserRepository userRepository;

    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll().stream().map(UserResponseDto::new).toList();
    }

    public UserResponseDto updateUserRole(Long userId, UserRole newRole) {
        User user = findUserById(userId);
        validateNotAdmin(user);
        user.setRole(newRole);
        userRepository.save(user);
        return new UserResponseDto(user);
    }

    public UserResponseDto updateUserNickname(Long userId,
        ChangeNicknameRequestDto changeNicknameRequestDto) {

        User user = findUserById(userId);
        validateNotAdmin(user);

        String newNickname = changeNicknameRequestDto.getNewNickname();
        user.setNickname(newNickname);
        userRepository.save(user);
        return new UserResponseDto(user);
    }

    public UserResponseDto blockUser(Long userId) {
        User user = findUserById(userId);
        validateNotAdmin(user);
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new InvalidOperationException("차단할 수 있는 상태가 아닙니다.");
        }
        return updateUserStatus(user, UserStatus.BLOCKED);
    }

    public UserResponseDto activateUser(Long userId) {
        User user = findUserById(userId);
        validateNotAdmin(user);
        if (user.getStatus() != UserStatus.BLOCKED) {
            throw new InvalidOperationException("활성화할 수 있는 상태가 아닙니다.");
        }
        return updateUserStatus(user, UserStatus.ACTIVE);
    }

    public UserResponseDto deleteUser(Long userId) {
        User user = findUserById(userId);
        validateNotAdmin(user);
        if (user.getStatus() != UserStatus.ACTIVE && user.getStatus() != UserStatus.BLOCKED) {
            throw new InvalidOperationException("삭제할 수 있는 상태가 아닙니다.");
        }
        return updateUserStatus(user, UserStatus.DELETED);
    }

    private User findUserById(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException(NotFoundEntity.USER_NOT_FOUND));
    }

    private UserResponseDto updateUserStatus(User user, UserStatus status) {
        user.setStatus(status);
        userRepository.save(user);
        return new UserResponseDto(user);
    }

    private void validateNotAdmin(User user) {
        if (user.getRole() == UserRole.ADMIN) {
            throw new InvalidOperationException("관리자의 상태는 변경할 수 없습니다.");
        }
    }
}