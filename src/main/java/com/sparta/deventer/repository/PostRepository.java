package com.sparta.deventer.repository;

import com.sparta.deventer.entity.Category;
import com.sparta.deventer.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PostRepository extends JpaRepository<Post, Long>, JpaSpecificationExecutor<Post> {

    Page<Post> findAllByCategory(Category category, Pageable pageable);

    Page<Post> findAll(Pageable pageable);

    Page<Post> findByUserId(Long userId, Pageable pageable);
}
