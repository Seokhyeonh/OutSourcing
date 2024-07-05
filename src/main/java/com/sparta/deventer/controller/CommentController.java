package com.sparta.deventer.controller;

import com.sparta.deventer.dto.CommentRequestDto;
import com.sparta.deventer.dto.CommentResponseDto;
import com.sparta.deventer.security.UserDetailsImpl;
import com.sparta.deventer.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentResponseDto> createComment(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody CommentRequestDto requestDto) {

        CommentResponseDto commentResponseDto = commentService.createComment(requestDto,
                userDetails.getUser());
        return ResponseEntity.ok().body(commentResponseDto);
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentResponseDto> updateComment(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @RequestBody CommentRequestDto requestDto,
            @PathVariable Long commentId) {
        return ResponseEntity.ok()
                .body(commentService.updateComment(userDetails.getUser().getId(), requestDto,
                        commentId));
    }

    @DeleteMapping("/{commentId}")
    public String deleteComment(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long commentId) {
        commentService.deleteComment(userDetails.getUser().getId(), commentId);
        return "삭제가 완료 되었습니다.";
    }
}
