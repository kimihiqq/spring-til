package me.kimihiqq.springtil.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.kimihiqq.springtil.domain.Board;
import me.kimihiqq.springtil.domain.BoardLike;
import me.kimihiqq.springtil.domain.Comment;
import me.kimihiqq.springtil.domain.User;
import me.kimihiqq.springtil.dto.AddBoardRequest;
import me.kimihiqq.springtil.dto.AddCommentRequest;
import me.kimihiqq.springtil.dto.UpdateBoardRequest;
import me.kimihiqq.springtil.dto.UpdateCommentRequest;
import me.kimihiqq.springtil.repository.BoardLikeRepository;
import me.kimihiqq.springtil.repository.CommentRepository;
import me.kimihiqq.springtil.repository.TilRepository;
import me.kimihiqq.springtil.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class TilService {

    private final TilRepository tilRepository;
    private final UserRepository userRepository;
    private final BoardLikeRepository boardLikeRepository;
    private final CommentRepository commentRepository;

    public Board save(AddBoardRequest request, String userName) {
        return tilRepository.save(request.toEntity(userName));
    }

    public List<Board> findAll() {
        return tilRepository.findAllWithComments();
    }

    public Board findById(long id) {
        return tilRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found : " + id));
    }

    public void delete(long id) {
        Board board = tilRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found : " + id));

        authorizeBoardAuthor(board);
        tilRepository.delete(board);
    }

    @Transactional
    public Board update(long id, UpdateBoardRequest request) {
        Board board = tilRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("not found : " + id));

        authorizeBoardAuthor(board);
        board.update(request.getTitle(), request.getContent());

        return board;
    }

    private static void authorizeBoardAuthor(Board board) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!board.getAuthor().equals(userName)) {
            throw new IllegalArgumentException("권한이 없는 접근입니다.");
        }
    }


    @Transactional
    public Comment saveComment(long boardId, AddCommentRequest request, String userName) {
        Board board = findById(boardId);
        User user = userRepository.findByEmail(userName)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userName));

        if (request.getContent() == null || request.getContent().isEmpty()) {
            throw new IllegalArgumentException("Comment content cannot be null or empty");
        }

        Comment comment = request.toEntity(board, user);
        board.addComment(comment);
        return commentRepository.save(comment);
    }



    public List<Comment> findCommentsByBoard(long boardId) {
        Board board = findById(boardId);
        return board.getComments();
    }

    private void authorizeCommentUser(Comment comment) {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!comment.getUser().getEmail().equals(userName)) {
            throw new IllegalArgumentException("해당 댓글의 처리에 대한 권한이 없습니다.");
        }
    }

    public void unlikeBoard(long boardId) {
        String userEmail = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userEmail));

        Board board = tilRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("Board not found: " + boardId));

        BoardLike boardLike = boardLikeRepository.findByBoardAndUser(board, user)
                .orElseThrow(() -> new IllegalArgumentException("BoardLike not found"));

        boardLikeRepository.delete(boardLike);
    }


    @Transactional
    public void toggleLikeBoard(long boardId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userEmail));

        Board board = tilRepository.findById(boardId)
                .orElseThrow(() -> new IllegalArgumentException("Board not found: " + boardId));

        Optional<BoardLike> boardLikeOptional = boardLikeRepository.findByBoardAndUser(board, user);
        if (boardLikeOptional.isPresent()) {
            boardLikeRepository.delete(boardLikeOptional.get());
        } else {
            boardLikeRepository.save(new BoardLike(user, board));
        }
    }

    public int countBoardLikes(Long boardId) {
        return boardLikeRepository.countByBoardId(boardId);
    }

    public Comment updateComment(long commentId, UpdateCommentRequest request) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found: " + commentId));

        authorizeCommentUser(comment);

        comment.updateContent(request.getContent());
        return commentRepository.save(comment);
    }

    @Transactional
    public void deleteComment(long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found: " + commentId));

        authorizeCommentUser(comment);

        Board board = comment.getBoard();
        board.removeComment(comment);
        commentRepository.delete(comment);
    }

}




