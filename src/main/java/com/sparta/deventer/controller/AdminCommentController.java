package com.sparta.deventer.controller;

import com.sparta.deventer.dto.CommentRequestDto;
import com.sparta.deventer.dto.CommentResponseDto;
import com.sparta.deventer.security.UserDetailsImpl;
import com.sparta.deventer.service.AdminCommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/admin/comments")
@RequiredArgsConstructor
public class AdminCommentController {

    private final AdminCommentService adminCommentService;

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentResponseDto> adminUpdateComment(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody CommentRequestDto requestDto,
            @PathVariable Long commentId) {
        return ResponseEntity.ok()
                .body(adminCommentService.updateComment(userDetails, requestDto,
                        commentId));
    }

    @DeleteMapping("/{commentId}")
    public String deleteComment(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long commentId) {
        adminCommentService.deleteComment(userDetails, commentId);
        return "삭제가 완료 되었습니다.";
    }
}
