package me.kimihiqq.springtil.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import me.kimihiqq.springtil.domain.Comment;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class CommentResponse {

    private long id;
    private String userName;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CommentResponse(Comment comment) {
        this.id = comment.getId();
        this.userName = comment.getUser().getNickname();
        this.content = comment.getContent();
        this.createdAt = comment.getCreatedAt();
        this.updatedAt = comment.getUpdatedAt();
    }

}
