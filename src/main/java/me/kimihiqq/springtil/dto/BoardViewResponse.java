package me.kimihiqq.springtil.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import me.kimihiqq.springtil.domain.Board;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
public class BoardViewResponse {

    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private String author;

    public BoardViewResponse(Board board) {
        this.id = board.getId();
        this.title = board.getTitle();
        this.content = board.getContent();
        this.createdAt = board.getCreatedAt();
        this.author = board.getAuthor();
    }
}
