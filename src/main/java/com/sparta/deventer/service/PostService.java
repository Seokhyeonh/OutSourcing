package com.sparta.deventer.service;

import com.sparta.deventer.dto.CommentResponseDto;
import com.sparta.deventer.dto.PostRequestDto;
import com.sparta.deventer.dto.PostResponseDto;
import com.sparta.deventer.dto.PostWithCommentsResponseDto;
import com.sparta.deventer.dto.UpdatePostRequestDto;
import com.sparta.deventer.entity.Category;
import com.sparta.deventer.entity.Comment;
import com.sparta.deventer.entity.Post;
import com.sparta.deventer.entity.User;
import com.sparta.deventer.enums.NotFoundEntity;
import com.sparta.deventer.enums.UserStatus;
import com.sparta.deventer.exception.EntityNotFoundException;
import com.sparta.deventer.repository.CategoryRepository;
import com.sparta.deventer.repository.CommentRepository;
import com.sparta.deventer.repository.PostRepository;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final CommentRepository commentRepository;

  private void UserNotBlocked(User user) {
    if (user.getStatus() == UserStatus.BLOCKED) {
      throw new EntityNotFoundException(NotFoundEntity.USER_NOT_FOUND);
    }
  }



    public PostWithCommentsResponseDto getPostDetail(Long postId) {
        List<Comment> commentList = commentRepository.findAllByPostId(postId);
        List<CommentResponseDto> commentResponseDtoList = new ArrayList<>();

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException(NotFoundEntity.POST_NOT_FOUND));

        PostResponseDto postResponseDto = new PostResponseDto(post);

        for (Comment comment : commentList) {
            CommentResponseDto commentResponseDto = new CommentResponseDto(comment);
            commentResponseDtoList.add(commentResponseDto);
        }
        PostWithCommentsResponseDto responseDto = new PostWithCommentsResponseDto(postResponseDto,
                commentResponseDtoList);
        return responseDto;
    }

    // 게시물 생성
    public PostResponseDto createPost(PostRequestDto postRequestDto, User user) {
      UserNotBlocked(user);
        Category category = categoryRepository.findByTopic(postRequestDto.getCategoryTopic())
                .orElseThrow(() -> new EntityNotFoundException(NotFoundEntity.CATEGORY_NOT_FOUND));

        Post post = new Post(postRequestDto.getTitle(), postRequestDto.getContent(), user,
                category);
        postRepository.save(post);
        return new PostResponseDto(post);
    }

    //게시물 전체 조회
    public List<PostResponseDto> getAllPosts(Pageable pageable) {
        Pageable sortedByCreatedAtDesc = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by("createdAt").descending()
        );
        Page<PostResponseDto> page = postRepository.findAll(sortedByCreatedAtDesc)
                .map(PostResponseDto::new);

        return page.getContent();
    }

    // 특정 카테고리 내의 모든 게시물 조회
    public List<PostResponseDto> getPostsByCategory(Long categoryId, Pageable pageable) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException(NotFoundEntity.CATEGORY_NOT_FOUND));

        Pageable sortedByCreatedAtDesc = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by("createdAt").descending()
        );
        Page<PostResponseDto> page = postRepository.findAllByCategory(category,
                        sortedByCreatedAtDesc)
                .map(PostResponseDto::new);

        return page.getContent();
    }

    // 게시글 수정
    public PostResponseDto updatePost(Long postId, UpdatePostRequestDto updatePostRequestsDto,
            User user) {
      UserNotBlocked(user);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException(NotFoundEntity.POST_NOT_FOUND));

        if (!post.getUser().getId().equals(user.getId())) {
            throw new EntityNotFoundException(NotFoundEntity.USER_NOT_FOUND);
        }

        post.update(
                updatePostRequestsDto.getTitle(),
                updatePostRequestsDto.getContent());

        postRepository.save(post);
        return new PostResponseDto(post);
    }

    //게시글 삭제
    public void deletePost(Long postId, User user) {
      UserNotBlocked(user);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new EntityNotFoundException(NotFoundEntity.POST_NOT_FOUND));

        if (!post.getUser().getId().equals(user.getId())) {
            throw new EntityNotFoundException(NotFoundEntity.USER_NOT_FOUND);
        }

        postRepository.delete(post);
    }
}