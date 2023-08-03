package me.kimihiqq.springtil.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.kimihiqq.springtil.domain.Board;
import me.kimihiqq.springtil.domain.Comment;
import me.kimihiqq.springtil.dto.*;
import me.kimihiqq.springtil.service.TilService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@RestController
public class TilApiController {

    private final TilService tilService;

    @PutMapping("/api/comments/{commentId}")
    public ResponseEntity<Comment> updateComment(@PathVariable long commentId,
                                                 @RequestBody UpdateCommentRequest request) {
        Comment updatedComment = tilService.updateComment(commentId, request);
        return ResponseEntity.ok().body(updatedComment);
    }

    @DeleteMapping("/api/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable long commentId) {
        tilService.deleteComment(commentId);
        return ResponseEntity.ok().build();
    }


    @PostMapping("/api/boards")
    public ResponseEntity<Board> addBoard(@RequestBody AddBoardRequest request, Principal principal) {
        Board savedBoard = tilService.save(request, principal.getName());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(savedBoard);
    }

    @GetMapping("/api/boards")
    public ResponseEntity<List<BoardResponse>> findAllBoards() {
        List<BoardResponse> boards = tilService.findAll()
                .stream()
                .map(BoardResponse::new)
                .toList();

        return ResponseEntity.ok()
                .body(boards);
    }
    @GetMapping("/api/boards/{id}")
    public ResponseEntity<BoardResponse> findBoard(@PathVariable long id) {
        Board board = tilService.findById(id);

        return ResponseEntity.ok()
                .body(new BoardResponse(board));
    }

    @DeleteMapping("/api/boards/{id}")
    public ResponseEntity<Void> deleteBoard(@PathVariable long id) {
        tilService.delete(id);

        return ResponseEntity.ok()
                .build();
    }

    @PutMapping("/api/boards/{id}")
    public ResponseEntity<Board> updateBoard(@PathVariable long id,
                                               @RequestBody UpdateBoardRequest request) {
        Board updatedBoard = tilService.update(id, request);

        return ResponseEntity.ok()
                .body(updatedBoard);
    }

    @PostMapping("/api/boards/{boardId}/comments")
    public ResponseEntity<Comment> addComment(@PathVariable long boardId, @RequestBody AddCommentRequest request, Principal principal) {
        log.info(request.getContent());
        Comment savedComment = tilService.saveComment(boardId, request, principal.getName());
        log.info(principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(savedComment);
    }


    @GetMapping("/api/boards/{boardId}/comments")
    public ResponseEntity<List<CommentResponse>> findCommentsByBoard(@PathVariable long boardId) {
        List<CommentResponse> comments = tilService.findCommentsByBoard(boardId)
                .stream()
                .map(CommentResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(comments);
    }




    @PostMapping("/api/boards/{boardId}/likes")
    public ResponseEntity<Void> likeBoard(@PathVariable long boardId, Principal principal) {
        tilService.toggleLikeBoard(boardId, principal.getName());
        return ResponseEntity.ok().build();
    }


    @DeleteMapping("/api/boards/{boardId}/likes")
    public ResponseEntity<Void> unlikeBoard(@PathVariable long boardId) {
        tilService.unlikeBoard(boardId);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/api/boards/{boardId}/likeCount")
    public ResponseEntity<Integer> getBoardLikeCount(@PathVariable Long boardId) {
        int likeCount = tilService.countBoardLikes(boardId);
        return ResponseEntity.ok().body(likeCount);
    }

}

