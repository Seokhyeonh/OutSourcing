package com.sparta.deventer.controller;

import com.sparta.deventer.entity.Comment;
import com.sparta.deventer.entity.Post;
import com.sparta.deventer.security.UserDetailsImpl;
import com.sparta.deventer.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class LikeController {

    private final LikeService likeService;

    @PostMapping("/likes")
    public ResponseEntity<String> isLike(@AuthenticationPrincipal UserDetailsImpl userDetails
            , @RequestParam("contentType") String contentType
            , @RequestParam("contentId") Long contentId) {
        Boolean isLiked = likeService.likeComparison(contentType, contentId,
                userDetails.getUser());
        String message = isLiked ? "좋아요가 완료 되었습니다." : "좋아요가 취소 되었습니다.";
        return ResponseEntity.ok()
                .body(message + "현재 좋아요 갯수 : " + likeService.likeCount(contentType, contentId));
    }
    /**
     * 사용자가 좋아요 한 게시글 목록 조회 엔드포인트.
     * @param userDetails 현재 인증된 사용자 정보
     * @param page 페이지 번호 (기본값: 0)
     * @param size 페이지 당 항목 수 (기본값: 5)
     * @return 좋아요한 게시글 목록
     */
    @GetMapping("/posts")
    public ResponseEntity<Page<Post>> getLikedPosts(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Post> likedPosts = likeService.getLikedPostsByUser(userDetails.getUser(), pageable);
        return ResponseEntity.ok(likedPosts);
    }

    /**
     * 사용자가 좋아요 한 댓글 목록 조회 엔드포인트.
     * @param userDetails 현재 인증된 사용자 정보
     * @param page 페이지 번호 (기본값: 0)
     * @param size 페이지 당 항목 수 (기본값: 5)
     * @return 좋아요한 댓글 목록
     */
    @GetMapping("/comments")
    public ResponseEntity<Page<Comment>> getLikedComments(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "5") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Comment> likedComments = likeService.getLikedCommentsByUser(userDetails.getUser(), pageable);
        return ResponseEntity.ok(likedComments);
    }
}
