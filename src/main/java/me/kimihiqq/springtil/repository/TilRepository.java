package me.kimihiqq.springtil.repository;

import me.kimihiqq.springtil.domain.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TilRepository extends JpaRepository<Board, Long> {
    @Query("SELECT distinct b FROM Board b LEFT JOIN FETCH b.comments")
    List<Board> findAllWithComments();

}


