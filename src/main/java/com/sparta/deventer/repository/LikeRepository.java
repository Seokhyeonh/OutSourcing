package com.sparta.deventer.repository;


import com.sparta.deventer.entity.ContentEnumType;
import com.sparta.deventer.entity.Like;
import com.sparta.deventer.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, Long> {

    Optional<Like> findByContentIdAndContentTypeAndUserId(Long contentId,
        ContentEnumType contentType,
        Long id);

    List<Like> findByContentIdAndContentType(Long contentId, ContentEnumType contentType);
    List<Like> findAllByContentIdAndContentType(Long contentId, ContentEnumType contentType);

    List<Like> findAllByUserAndContentType(User user, ContentEnumType contentType);
}
