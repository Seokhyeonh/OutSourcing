package com.sparta.deventer.service;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.deventer.dto.ChangePasswordRequestDto;
import com.sparta.deventer.dto.CommentResponseDto;
import com.sparta.deventer.dto.PostResponseDto;
import com.sparta.deventer.dto.ProfileResponseDto;
import com.sparta.deventer.dto.UpdateProfileRequestDto;
import com.sparta.deventer.entity.ContentEnumType;
import com.sparta.deventer.entity.PasswordHistory;
import com.sparta.deventer.entity.QLike;
import com.sparta.deventer.entity.User;
import com.sparta.deventer.exception.InvalidPasswordException;
import com.sparta.deventer.repository.CommentRepository;
import com.sparta.deventer.repository.PasswordHistoryRepository;
import com.sparta.deventer.repository.PostRepository;
import com.sparta.deventer.repository.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final PasswordHistoryRepository passwordHistoryRepository;
    private final PasswordEncoder passwordEncoder;
    private final JPAQueryFactory queryFactory;

    public ProfileResponseDto getProfile(Long userId, User user) {
        user.validateId(userId);

        QLike qLike = QLike.like;

        // Count liked posts
        int likedPostsCount = (int) queryFactory
            .selectFrom(qLike)
            .where(qLike.user.id.eq(userId)
                .and(qLike.contentType.eq(ContentEnumType.POST)))
            .fetchCount();

        // Count liked comments
        int likedCommentsCount = (int) queryFactory
            .selectFrom(qLike)
            .where(qLike.user.id.eq(userId)
                .and(qLike.contentType.eq(ContentEnumType.COMMENT)))
            .fetchCount();

        return new ProfileResponseDto(user, likedPostsCount, likedCommentsCount);
    }

    public Page<PostResponseDto> getAllPosts(Long userId, User user, Pageable pageable) {
        user.validateId(userId);
        return postRepository.findByUserId(userId, pageable).map(PostResponseDto::new);
    }

    public Page<CommentResponseDto> getAllComments(Long userId, User user, Pageable pageable) {
        user.validateId(userId);
        return commentRepository.findByUserId(userId, pageable).map(CommentResponseDto::new);
    }

    public ProfileResponseDto updateProfile(Long userId,
        UpdateProfileRequestDto updateProfileRequestDto, User user) {

        user.validateId(userId);

        user.setNickname(updateProfileRequestDto.getNickname());
        user.setEmail(updateProfileRequestDto.getEmail());
        userRepository.save(user);
        return new ProfileResponseDto(user);
    }

    public void changePassword(Long userId, ChangePasswordRequestDto changePasswordRequestDto,
        User user) {

        user.validateId(userId);

        String currentPassword = changePasswordRequestDto.getCurrentPassword();
        String newPassword = changePasswordRequestDto.getNewPassword();
        List<PasswordHistory> passwordHistoryList =
            passwordHistoryRepository.findByUserOrderByCreatedAtAsc(user);

        user.validatePassword(passwordEncoder, currentPassword);
        validateNewPassword(user, newPassword);
        validatePasswordHistory(newPassword, passwordHistoryList);

        PasswordHistory newHistory = new PasswordHistory(passwordEncoder.encode(newPassword), user);
        passwordHistoryRepository.save(newHistory);

        if (passwordHistoryList.size() > 2) {
            passwordHistoryRepository.delete(passwordHistoryList.get(0));
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    private void validateNewPassword(User user, String newPassword) {
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new InvalidPasswordException("새 비밀번호는 현재 비밀번호와 달라야 합니다.");
        }
    }

    public void validatePasswordHistory(String newPassword,
        List<PasswordHistory> passwordHistoryList) {

        for (PasswordHistory passwordHistory : passwordHistoryList) {
            if (passwordEncoder.matches(newPassword, passwordHistory.getPassword())) {
                throw new InvalidPasswordException("새 비밀번호는 최근 사용한 비밀번호와 달라야 합니다.");
            }
        }
    }
}