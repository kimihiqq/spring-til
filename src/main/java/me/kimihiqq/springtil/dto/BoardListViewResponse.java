package me.kimihiqq.springtil.dto;

import lombok.Getter;
import me.kimihiqq.springtil.domain.Board;

import java.time.LocalDateTime;

@Getter
public class BoardListViewResponse {

    private final Long id;
    private final String title;
    private final String author;
    private LocalDateTime createdAt;

    public BoardListViewResponse(Board board) {
        this.id = board.getId();
        this.title = board.getTitle();
        this.author = board.getAuthor();
        this.createdAt = board.getCreatedAt();
    }
}

