package com.sparta.deventer.dto;


import com.sparta.deventer.entity.Comment;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
public class CommentResponseDto {

    private String content;
    private Long id;
    private Long userId;
    private String nickNmae;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;


    public CommentResponseDto(Comment comment) {
        this.content = comment.getContent();
        this.id = comment.getId();
        this.userId = comment.getUser().getId();
        this.nickNmae = comment.getUser().getNickname();
        this.createAt = comment.getCreatedAt();
        this.updateAt = comment.getUpdateAt();
    }
}
