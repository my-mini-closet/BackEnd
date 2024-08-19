package com.example.mycloset.controller;

import com.example.mycloset.dto.BoardDTO;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import com.example.mycloset.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;

    @PostMapping("/createwithimage")
    public BoardDTO createBoardWithImages(
            @RequestParam("title") String title,
            @RequestParam("text") String text,
            @RequestParam("userId") Long userId,
            @RequestParam("files") List<MultipartFile> files) throws IOException {
        return boardService.saveBoardWithImages(title, text, userId, files);
    }

    @GetMapping("/page")
    public Page<BoardDTO> getBoardsByPage(
            @RequestParam int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        return boardService.getBoardsByPage(page, pageSize);
    }

    @PostMapping("/create")
    public BoardDTO createBoard(
            @RequestParam String title,
            @RequestParam String text,
            @RequestParam Long userId) {
        return boardService.createBoard(title, text, userId);
    }

    @GetMapping("/{id}/{category}")
    public List<BoardDTO> getCategoryBoards(@PathVariable Long id, @PathVariable String category) {return boardService.getBoardsByCategory(id, category); }
    @GetMapping("/all")
    public List<BoardDTO> getAllBoards() {
        return boardService.getAllBoards();
    }

    @GetMapping("/{id}")
    public BoardDTO getBoardById(@PathVariable Long id) {
        return boardService.getBoardById(id);
    }

    @PutMapping("/{id}/update")
    public BoardDTO updateBoard(
            @PathVariable Long id,
            @RequestParam String title,
            @RequestParam String text) {
        return boardService.updateBoard(id, title, text);
    }

    @PostMapping("/{id}/like")
    public BoardDTO likeBoard(@PathVariable Long id, @RequestParam Long userId) {
        return boardService.likeBoard(id, userId);
    }

    @PostMapping("/{id}/unlike")
    public BoardDTO unlikeBoard(@PathVariable Long id, @RequestParam Long userId) {
        return boardService.unlikeBoard(id, userId);
    }

    @DeleteMapping("/{id}/delete")
    public void deleteBoard(@PathVariable Long id) {
        boardService.deleteBoard(id);
    }

}
