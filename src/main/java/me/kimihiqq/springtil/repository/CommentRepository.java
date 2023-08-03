package me.kimihiqq.springtil.repository;

import me.kimihiqq.springtil.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}


