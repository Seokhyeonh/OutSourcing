package com.sparta.deventer.controller;

import com.sparta.deventer.dto.MoveCategoryRequestDto;
import com.sparta.deventer.dto.PostRequestDto;
import com.sparta.deventer.dto.PostResponseDto;
import com.sparta.deventer.dto.UpdatePostRequestDto;
import com.sparta.deventer.security.UserDetailsImpl;
import com.sparta.deventer.service.AdminPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminPostController {

    private final AdminPostService adminPostService;

    // 공지글 생성
    @PutMapping("/posts/notice")
    public ResponseEntity<PostResponseDto> createNoticePost(
            @RequestBody PostRequestDto postRequestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        PostResponseDto postResponseDto = adminPostService.createNoticePost(postRequestDto,
                userDetails.getUser());
        return ResponseEntity.ok().body(postResponseDto);
    }

    // 공지글 수정
    @PutMapping("/posts/notice/{postId}")
    public ResponseEntity<PostResponseDto> updateNoticePost(
            @PathVariable Long postId,
            @RequestBody UpdatePostRequestDto updatePostRequestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        PostResponseDto postResponseDto = adminPostService.updateNoticePost(postId,
                updatePostRequestDto, userDetails.getUser());
        return ResponseEntity.ok(postResponseDto);
    }

    // 어드민 권한으로 게시물 삭제
    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<Void> deleteAnyPost(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        adminPostService.deleteUserPost(postId, userDetails.getUser());
        return ResponseEntity.noContent().build();
    }

    //카테고리 이동
    @PutMapping("/posts/{postId}")
    public ResponseEntity<PostResponseDto> moveCategory(
            @PathVariable Long postId,
            @RequestBody MoveCategoryRequestDto moveCategoryRequestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        PostResponseDto postResponseDto = adminPostService.moveCategory(postId,
                moveCategoryRequestDto,
                userDetails.getUser());
        return ResponseEntity.ok(postResponseDto);
    }

}