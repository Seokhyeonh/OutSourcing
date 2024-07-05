package com.sparta.deventer.service;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.deventer.entity.Comment;
import com.sparta.deventer.entity.ContentEnumType;
import com.sparta.deventer.entity.Like;
import com.sparta.deventer.entity.Post;
import com.sparta.deventer.entity.QComment;
import com.sparta.deventer.entity.QLike;
import com.sparta.deventer.entity.QPost;
import com.sparta.deventer.entity.User;
import com.sparta.deventer.enums.MismatchStatusEntity;
import com.sparta.deventer.enums.NotFoundEntity;
import com.sparta.deventer.exception.EntityNotFoundException;
import com.sparta.deventer.exception.MismatchStatusException;
import com.sparta.deventer.repository.CommentRepository;
import com.sparta.deventer.repository.LikeRepository;
import com.sparta.deventer.repository.PostRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    private final JPAQueryFactory  queryFactory;

    public Boolean likeComparison(String contentType, Long contentId, User user) {
        Optional<Like> like = likeRepository.findByContentIdAndContentTypeAndUserId(contentId,
                ContentEnumType.getByType(contentType), user.getId());

        if (like.isEmpty()) {
            CheckContent(contentType, contentId, user.getId());
            Like saveLike = new Like(user, contentId, contentType);
            likeRepository.save(saveLike);
            return true;
        } else {
            CheckContent(contentType, contentId, user.getId());
            likeRepository.delete(like.get());
            return false;
        }
    }

    public int likeCount(String contentType, Long contentId) {
        return likeRepository.findByContentIdAndContentType(contentId,
                ContentEnumType.getByType(contentType)).size();
    }

    public void CheckContent(String contentType, Long contentId, Long userId) {
        if (contentType.equals(ContentEnumType.POST.getType())) {
            Post post = postRepository.findById(contentId).orElseThrow(
                    () -> new EntityNotFoundException(NotFoundEntity.POST_NOT_FOUND));
            if (post.getUser().getId().equals(userId)) {
                throw new MismatchStatusException(MismatchStatusEntity.SELF_USER);
            }
        } else {
            Comment comment = commentRepository.findById(contentId).orElseThrow(
                    () -> new EntityNotFoundException(NotFoundEntity.COMMENT_NOT_FOUND));
            if (comment.getUser().getId().equals(userId)) {
                throw new MismatchStatusException(MismatchStatusEntity.SELF_USER);
            }
        }
    }

    /**
     * 사용자가 좋아요 한 게시글 목록을 페이지네이션하여 반환합니다.
     * @param user 현재 인증된 사용자
     * @param pageable 페이지네이션 정보
     * @return 좋아요한 게시글 목록
     */
    public Page<Post> getLikedPostsByUser(User user, Pageable pageable) {
        List<Post> likedPosts = queryFactory.select(QPost.post)
            .from(QLike.like)
            .innerJoin(QPost.post)
            .on(QPost.post.id.eq(QLike.like.contentId)
                .and(QLike.like.user.eq(user))
                .and(QLike.like.contentType.eq(ContentEnumType.POST)))
            .orderBy(QPost.post.createdAt.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        long total = queryFactory.selectFrom(QLike.like)
            .where(QLike.like.user.eq(user)
                .and(QLike.like.contentType.eq(ContentEnumType.POST)))
            .fetchCount();

        return new PageImpl<>(likedPosts, pageable, total);
    }

    /**
     * 사용자가 좋아요 한 댓글 목록을 페이지네이션하여 반환합니다.
     * @param user 현재 인증된 사용자
     * @param pageable 페이지네이션 정보
     * @return 좋아요한 댓글 목록
     */
    public Page<Comment> getLikedCommentsByUser(User user, Pageable pageable) {
        List<Comment> likedComments = queryFactory.select(QComment.comment)
            .from(QLike.like)
            .innerJoin(QComment.comment)
            .on(QComment.comment.id.eq(QLike.like.contentId)
                .and(QLike.like.user.eq(user))
                .and(QLike.like.contentType.eq(ContentEnumType.COMMENT)))
            .orderBy(QComment.comment.createdAt.desc())
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        long total = queryFactory.selectFrom(QLike.like)
            .where(QLike.like.user.eq(user)
                .and(QLike.like.contentType.eq(ContentEnumType.COMMENT)))
            .fetchCount();

        return new PageImpl<>(likedComments, pageable, total);
    }
}

