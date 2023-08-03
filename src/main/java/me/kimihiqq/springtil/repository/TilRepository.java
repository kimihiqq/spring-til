package me.kimihiqq.springtil.repository;

import me.kimihiqq.springtil.domain.Board;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TilRepository extends JpaRepository<Board, Long> {
}


