package com.sparta.deventer.controller;

import com.sparta.deventer.dto.ChangeNicknameRequestDto;
import com.sparta.deventer.dto.UserResponseDto;
import com.sparta.deventer.enums.UserRole;
import com.sparta.deventer.service.AdminUserService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/users")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class AdminUserController {

    private final AdminUserService adminUserService;

    // (관리자) 사용자 전체 목록 조회
    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers() {
        List<UserResponseDto> users = adminUserService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // (관리자) 사용자 권한 부여
    @PutMapping("/{userId}/role")
    public ResponseEntity<UserResponseDto> updateUserRole(@PathVariable Long userId) {

        UserResponseDto userResponseDto = adminUserService.updateUserRole(userId, UserRole.ADMIN);
        return ResponseEntity.ok(userResponseDto);
    }

    // (관리자) 사용자 닉네임 수정
    @PutMapping("/{userId}/nickname")
    public ResponseEntity<UserResponseDto> updateUserNickname(@PathVariable Long userId,
        @RequestBody ChangeNicknameRequestDto changeNicknameRequestDto) {

        UserResponseDto userResponseDto = adminUserService.updateUserNickname(userId,
            changeNicknameRequestDto);
        return ResponseEntity.ok(userResponseDto);
    }

    // (관리자) 사용자 차단
    @PutMapping("/{userId}/block")
    public ResponseEntity<UserResponseDto> blockUser(@PathVariable Long userId) {

        UserResponseDto userResponseDto = adminUserService.blockUser(userId);
        return ResponseEntity.ok(userResponseDto);
    }

    // (관리자) 사용자 활성화
    @PutMapping("/{userId}/activate")
    public ResponseEntity<UserResponseDto> activateUser(@PathVariable Long userId) {

        UserResponseDto userResponseDto = adminUserService.activateUser(userId);
        return ResponseEntity.ok(userResponseDto);
    }

    // (관리자) 사용자 삭제
    @PutMapping("/{userId}/delete")
    public ResponseEntity<UserResponseDto> deleteUser(@PathVariable Long userId) {

        UserResponseDto userResponseDto = adminUserService.deleteUser(userId);
        return ResponseEntity.ok(userResponseDto);
    }
}