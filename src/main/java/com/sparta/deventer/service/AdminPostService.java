package com.sparta.deventer.service;

import com.sparta.deventer.dto.MoveCategoryRequestDto;
import com.sparta.deventer.dto.PostRequestDto;
import com.sparta.deventer.dto.PostResponseDto;
import com.sparta.deventer.dto.UpdatePostRequestDto;
import com.sparta.deventer.entity.Category;
import com.sparta.deventer.entity.Post;
import com.sparta.deventer.entity.User;
import com.sparta.deventer.enums.NotFoundEntity;
import com.sparta.deventer.enums.UserRole;
import com.sparta.deventer.exception.EntityNotFoundException;
import com.sparta.deventer.exception.NotAdminException;
import com.sparta.deventer.repository.CategoryRepository;
import com.sparta.deventer.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AdminPostService {

    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;


    private void checkAdmin(User user) {
        if (user.getRole() != UserRole.ADMIN) {
            throw new NotAdminException("관리자 권한이 필요합니다.");
        }
    }

    // 공지글 생성
    public PostResponseDto createNoticePost(PostRequestDto postRequestDto, User admin) {
        checkAdmin(admin);

        Category category = categoryRepository.findByTopic(postRequestDto.getCategoryTopic())
                .orElseThrow(() -> new EntityNotFoundException(NotFoundEntity.CATEGORY_NOT_FOUND));

        Post post = new Post(postRequestDto.getTitle(), postRequestDto.getContent(), admin, category);
        post.setNotice(true); // 공지글로 설정
        postRepository.save(post);
        return new PostResponseDto(post);
    }

    // 공지글 수정
    public PostResponseDto updateNoticePost(Long postId, UpdatePostRequestDto updatePostRequestDto, User admin) {
        checkAdmin(admin);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException(NotFoundEntity.POST_NOT_FOUND));

        if (!post.isNotice()) {
            throw new IllegalArgumentException("공지글이 아닙니다.");
        }

        post.update(updatePostRequestDto.getTitle(), updatePostRequestDto.getContent());
        postRepository.save(post);
        return new PostResponseDto(post);
    }

    // 어드민 권한으로 게시물 삭제
    public void deleteUserPost(Long postId, User admin) {
        checkAdmin(admin);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException(NotFoundEntity.POST_NOT_FOUND));

        postRepository.delete(post);
    }
    //카테고리 이동
    public PostResponseDto moveCategory(Long postId, MoveCategoryRequestDto moveCategoryRequestDto, User admin) {
        checkAdmin(admin);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException(NotFoundEntity.POST_NOT_FOUND));

        Category newCategory = categoryRepository.findByTopic(moveCategoryRequestDto.getCategoryTopic())
                .orElseThrow(() -> new EntityNotFoundException(NotFoundEntity.CATEGORY_NOT_FOUND));

        post.setCategory(newCategory);
        postRepository.save(post);

        return new PostResponseDto(post);
    }
}