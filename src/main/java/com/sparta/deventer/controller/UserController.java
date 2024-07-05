package com.sparta.deventer.controller;

import com.sparta.deventer.dto.ChangePasswordRequestDto;
import com.sparta.deventer.dto.CommentResponseDto;
import com.sparta.deventer.dto.PostResponseDto;
import com.sparta.deventer.dto.ProfileResponseDto;
import com.sparta.deventer.dto.UpdateProfileRequestDto;
import com.sparta.deventer.entity.User;
import com.sparta.deventer.security.UserDetailsImpl;
import com.sparta.deventer.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}")
public class UserController {

    private static final int PAGE_SIZE = 5;
    private final UserService userService;

    @GetMapping
    public ResponseEntity<Object> getProfile(@PathVariable Long userId,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {

        User user = userDetails.getUser();
        ProfileResponseDto profileResponseDto = userService.getProfile(userId, user);
        return ResponseEntity.ok(profileResponseDto);
    }

    @GetMapping("/posts")
    public ResponseEntity<Object> getAllPosts(@PathVariable Long userId,
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestParam(defaultValue = "0") int page) {

        User user = userDetails.getUser();
        Pageable pageable = PageRequest.of(page, PAGE_SIZE);
        Page<PostResponseDto> postResponseDtoPage = userService.getAllPosts(userId, user, pageable);
        return ResponseEntity.ok(postResponseDtoPage);
    }

    @GetMapping("/comments")
    public ResponseEntity<Object> getAllComments(@PathVariable Long userId,
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestParam(defaultValue = "0") int page) {

        User user = userDetails.getUser();
        Pageable pageable = PageRequest.of(page, PAGE_SIZE);
        Page<CommentResponseDto> commentResponseDtoPage = userService.getAllComments(userId, user,
            pageable);
        return ResponseEntity.ok(commentResponseDtoPage);
    }

    @PutMapping
    public ResponseEntity<Object> updateProfile(@PathVariable Long userId,
        @RequestBody UpdateProfileRequestDto updateProfileRequestDto,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {

        User user = userDetails.getUser();
        ProfileResponseDto profileResponseDto = userService.updateProfile(userId,
            updateProfileRequestDto, user);
        return ResponseEntity.ok(profileResponseDto);
    }

    @PutMapping("/change-password")
    public ResponseEntity<Object> changePassword(@PathVariable Long userId,
        @RequestBody ChangePasswordRequestDto changePasswordRequestDto,
        @AuthenticationPrincipal UserDetailsImpl userDetails) {

        User user = userDetails.getUser();
        userService.changePassword(userId, changePasswordRequestDto, user);
        return ResponseEntity.noContent().build();
    }
}