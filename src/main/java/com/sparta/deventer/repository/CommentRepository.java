package com.sparta.deventer.repository;

import com.sparta.deventer.entity.Comment;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByPostId(long id);

    Page<Comment> findByUserId(Long userId, Pageable pageable);
}