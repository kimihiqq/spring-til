package me.kimihiqq.springtil.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.kimihiqq.springtil.domain.Board;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AddBoardRequest {

    private String title;
    private String content;

    public Board toEntity(String author) {
        return Board.builder()
                .title(title)
                .content(content)
                .author(author)
                .build();
    }
}

