package me.kimihiqq.springtil.dto;

import lombok.Getter;
import me.kimihiqq.springtil.domain.Board;

@Getter
public class BoardResponse {

    private final String title;
    private final String content;

    public BoardResponse(Board board) {
        this.title = board.getTitle();
        this.content = board.getContent();
    }
}
