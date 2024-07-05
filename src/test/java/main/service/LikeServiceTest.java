package main.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import org.springframework.boot.test.context.SpringBootTest;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.deventer.entity.*;
import com.sparta.deventer.enums.MismatchStatusEntity;
import com.sparta.deventer.exception.MismatchStatusException;
import com.sparta.deventer.repository.CommentRepository;
import com.sparta.deventer.repository.LikeRepository;
import com.sparta.deventer.repository.PostRepository;

import com.sparta.deventer.service.LikeService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@SpringBootTest
public class LikeServiceTest {

  @Mock
  private PostRepository postRepository;

  @Mock
  private CommentRepository commentRepository;

  @Mock
  private LikeRepository likeRepository;

  @Mock
  private JPAQueryFactory queryFactory;

  @InjectMocks
  private LikeService likeService;

  private User testUser;
  private Post testPost;
  private Comment testComment;

  @BeforeEach
  public void setUp() {
    testUser = new User();
    testUser.setId(1L);

    testPost = new Post();
    testPost.setId(1L);
    testPost.setUser(testUser);

    testComment = new Comment();
    testComment.setId(1L);
    testComment.setUser(testUser);
  }

  @Test
  public void likeComparison_NotAlreadyLiked() {
    // Arrange
    String contentType = ContentEnumType.POST.getType();
    Long contentId = testPost.getId();

    when(likeRepository.findByContentIdAndContentTypeAndUserId(contentId, ContentEnumType.POST, testUser.getId()))
        .thenReturn(Optional.empty());
    when(postRepository.findById(contentId)).thenReturn(Optional.of(testPost));

    // Act
    Boolean isLiked = likeService.likeComparison(contentType, contentId, testUser);

    // Assert
    assertThat(isLiked).isTrue();
    verify(likeRepository, times(1)).save(any(Like.class));
    verify(likeRepository, never()).delete(any(Like.class));
  }

  @Test
  public void likeComparison_AlreadyLiked() {

    String contentType = ContentEnumType.POST.getType();
    Long contentId = testPost.getId();
    Like existingLike = new Like(testUser, contentId, contentType);

    when(likeRepository.findByContentIdAndContentTypeAndUserId(contentId, ContentEnumType.POST, testUser.getId()))
        .thenReturn(Optional.of(existingLike));
    when(postRepository.findById(contentId)).thenReturn(Optional.of(testPost));

    Boolean isLiked = likeService.likeComparison(contentType, contentId, testUser);

    assertThat(isLiked).isFalse();
    verify(likeRepository, times(1)).delete(existingLike);
    verify(likeRepository, never()).save(any(Like.class));
  }

  @Test
  public void likeComparison_PostLike() {

    String contentType = ContentEnumType.POST.getType();
    Long contentId = testPost.getId();

    when(likeRepository.findByContentIdAndContentTypeAndUserId(contentId, ContentEnumType.POST, testUser.getId()))
        .thenReturn(Optional.empty());
    when(postRepository.findById(contentId)).thenReturn(Optional.of(testPost));


    try {
      likeService.likeComparison(contentType, contentId, testUser);
    } catch (MismatchStatusException e) {
      assertThat(e.getMessage()).isEqualTo(MismatchStatusEntity.SELF_USER.name());
    }
    verify(likeRepository, never()).save(any(Like.class));
  }

  @Test
  public void likeCount_Count() {

    String contentType = ContentEnumType.POST.getType();
    Long contentId = testPost.getId();

    List<Like> likes = new ArrayList<>();
    likes.add(new Like(testUser, contentId, contentType));
    likes.add(new Like(testUser, contentId, contentType));

    when(likeRepository.findByContentIdAndContentType(contentId, ContentEnumType.POST)).thenReturn(likes);


    int count = likeService.likeCount(contentType, contentId);


    assertThat(count).isEqualTo(2);
  }

  @Test
  public void getLikedPostsByUser_PaginatedPosts() {
    // Arrange
    List<Post> posts = new ArrayList<>();
    posts.add(testPost);

    Pageable pageable = PageRequest.of(0, 10);

    when(queryFactory.select(QPost.post)
        .from(QLike.like)
        .innerJoin(QPost.post)
        .on(QPost.post.id.eq(QLike.like.contentId)
            .and(QLike.like.user.eq(testUser))
            .and(QLike.like.contentType.eq(ContentEnumType.POST)))
        .orderBy(QPost.post.createdAt.desc())
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch()).thenReturn(posts);

    when(queryFactory.selectFrom(QLike.like)
        .where(QLike.like.user.eq(testUser)
            .and(QLike.like.contentType.eq(ContentEnumType.POST)))
        .fetchCount()).thenReturn(1L);


    Page<Post> likedPosts = likeService.getLikedPostsByUser(testUser, pageable);


    assertThat(likedPosts.getTotalElements()).isEqualTo(1);
    assertThat(likedPosts.getContent()).isEqualTo(posts);
  }

  @Test
  public void getLikedCommentsByUser_PaginatedComments() {

    List<Comment> comments = new ArrayList<>();
    comments.add(testComment);

    Pageable pageable = PageRequest.of(0, 10);

    when(queryFactory.select(QComment.comment)
        .from(QLike.like)
        .innerJoin(QComment.comment)
        .on(QComment.comment.id.eq(QLike.like.contentId)
            .and(QLike.like.user.eq(testUser))
            .and(QLike.like.contentType.eq(ContentEnumType.COMMENT)))
        .orderBy(QComment.comment.createdAt.desc())
        .offset(pageable.getOffset())
        .limit(pageable.getPageSize())
        .fetch()).thenReturn(comments);

    when(queryFactory.selectFrom(QLike.like)
        .where(QLike.like.user.eq(testUser)
            .and(QLike.like.contentType.eq(ContentEnumType.COMMENT)))
        .fetchCount()).thenReturn(1L);


    Page<Comment> likedComments = likeService.getLikedCommentsByUser(testUser, pageable);

    assertThat(likedComments.getTotalElements()).isEqualTo(1);
    assertThat(likedComments.getContent()).isEqualTo(comments);
  }
}
