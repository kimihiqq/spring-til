package me.kimihiqq.springtil.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.kimihiqq.springtil.domain.Board;
import me.kimihiqq.springtil.dto.BoardListViewResponse;
import me.kimihiqq.springtil.dto.BoardViewResponse;
import me.kimihiqq.springtil.service.TilService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Controller
public class TilViewController {

    private final TilService tilService;

    @GetMapping("/boards/{id}")
    public String getBoard(@PathVariable Long id, Model model) {
        Board board = tilService.findById(id);
        model.addAttribute("board", new BoardViewResponse(board));

        return "board";
    }
    @GetMapping("/")
    public String defaultPage() {
        return "redirect:/boards";
    }

    @GetMapping("/boards")
    public String getBoards(Model model) {
        List<BoardListViewResponse> boards = tilService.findAll()
                .stream()
                .map(BoardListViewResponse::new)
                .toList();
        model.addAttribute("boards", boards);

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("Current authenticated user: {}", username);

        return "boardList";
    }




    @GetMapping("/new-board")
    public String newBoard(@RequestParam(required = false) Long id, Model model) {
        if (id == null) {
            model.addAttribute("board", new BoardViewResponse());
        } else {
            Board board = tilService.findById(id);
            model.addAttribute("board", new BoardViewResponse(board));
        }

        return "newBoard";
    }
}
