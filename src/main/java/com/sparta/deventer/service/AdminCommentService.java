package com.sparta.deventer.service;

import com.sparta.deventer.dto.CommentRequestDto;
import com.sparta.deventer.dto.CommentResponseDto;
import com.sparta.deventer.entity.Comment;
import com.sparta.deventer.enums.UserRole;
import com.sparta.deventer.repository.CommentRepository;
import com.sparta.deventer.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminCommentService {

    private final CommentRepository commentRepository;

    @Transactional
    public CommentResponseDto updateComment(UserDetailsImpl userDetails,
            CommentRequestDto requestDto, Long commentId) {
        checkAdminUser(userDetails.getUser().getRole());

        Comment comment = emptyCheckComment(commentId);

        checkEqualsPost(requestDto.getPostId(), comment.getPost().getId());

        comment.update(requestDto.getContent());

        return new CommentResponseDto(comment);
    }

    public void deleteComment(UserDetailsImpl userDetails, Long commentId) {
        checkAdminUser(userDetails.getUser().getRole());

        Comment comment = emptyCheckComment(commentId);

        commentRepository.delete(comment);
    }


    public void checkAdminUser(UserRole role) {
        if (!role.equals(UserRole.ADMIN)) {
            throw new IllegalArgumentException("관리자가 아닙니다.");
        }
    }

    public void checkEqualsPost(Long postId, Long id) {
        if (!postId.equals(id)) {
            throw new IllegalArgumentException("게시글이 다릅니다");
        }
    }

    public Comment emptyCheckComment(Long commentId) {
        return commentRepository.findById(commentId).orElseThrow(
                () -> new IllegalArgumentException("댓글이 존재 하지 않습니다."));
    }


}
