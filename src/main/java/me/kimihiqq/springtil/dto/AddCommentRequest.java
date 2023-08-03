package me.kimihiqq.springtil.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.kimihiqq.springtil.domain.Board;
import me.kimihiqq.springtil.domain.Comment;
import me.kimihiqq.springtil.domain.User;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AddCommentRequest {

    private String content;

    public Comment toEntity(Board board, User user) {
        return Comment.builder()
                .board(board)
                .user(user)
                .content(content)
                .build();
    }
}