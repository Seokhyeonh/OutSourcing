package com.sparta.deventer.service;

import com.sparta.deventer.dto.CommentRequestDto;
import com.sparta.deventer.dto.CommentResponseDto;
import com.sparta.deventer.entity.Comment;
import com.sparta.deventer.entity.Post;
import com.sparta.deventer.entity.User;
import com.sparta.deventer.enums.MismatchStatusEntity;
import com.sparta.deventer.enums.NotFoundEntity;
import com.sparta.deventer.enums.UserStatus;
import com.sparta.deventer.exception.EntityNotFoundException;
import com.sparta.deventer.exception.MismatchStatusException;
import com.sparta.deventer.repository.CommentRepository;
import com.sparta.deventer.repository.PostRepository;
import com.sparta.deventer.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public CommentResponseDto createComment(CommentRequestDto requestDto, User user) {
        checkUserStatus(user.getId());

        Post post = checkEmptyPost(requestDto.getPostId());

        Comment comment = new Comment(post, user, requestDto.getContent());

        commentRepository.save(comment);
        return new CommentResponseDto(comment);

    }

    @Transactional
    public CommentResponseDto updateComment(Long userId, CommentRequestDto requestDto,
            Long commentId) {
        checkUserStatus(userId);
        Comment comment = emptyCheckComment(commentId);

        checkEqualsUser(comment, userId);

        comment.update(requestDto.getContent());

        return new CommentResponseDto(comment);
    }

    public void deleteComment(Long userId, Long commentId) {
        checkUserStatus(userId);
        Comment comment = emptyCheckComment(commentId);

        checkEqualsUser(comment, userId);

        commentRepository.delete(comment);
    }

    //댓글이 이미 사라졌는지 확인
    public Comment emptyCheckComment(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException(NotFoundEntity.COMMENT_NOT_FOUND));
    }

    //본인인지 아닌지 확인
    public void checkEqualsUser(Comment comment, Long userId) {
        if (!comment.getUser().getId().equals(userId)) {
            throw new MismatchStatusException(MismatchStatusEntity.MISMATCH_USER);
        }
    }

    //작업전 게시글이 있는지 확인
    public Post checkEmptyPost(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException(NotFoundEntity.POST_NOT_FOUND));
    }

    //BLOCKED 유저 체크
    public void checkUserStatus(Long userId) {
        User checkUser = userRepository.findById((userId)).orElseThrow(
                () -> new EntityNotFoundException(NotFoundEntity.USER_NOT_FOUND));
        if (checkUser.getStatus().equals(UserStatus.BLOCKED)) {
            throw new MismatchStatusException(MismatchStatusEntity.MISMATCH_STATUS_BLOCKED_USER);
        }
    }

}
