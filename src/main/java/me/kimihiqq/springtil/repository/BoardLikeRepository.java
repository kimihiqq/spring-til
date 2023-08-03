package me.kimihiqq.springtil.repository;

import me.kimihiqq.springtil.domain.Board;
import me.kimihiqq.springtil.domain.BoardLike;
import me.kimihiqq.springtil.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BoardLikeRepository extends JpaRepository<BoardLike, Long> {
    Optional<BoardLike> findByBoardAndUser(Board board, User user);

    int countByBoardId(Long boardId);

}
