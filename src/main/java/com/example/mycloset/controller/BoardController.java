package com.example.mycloset.controller;

<<<<<<< HEAD
import com.example.mycloset.dto.BoardDTO;
import com.example.mycloset.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

=======
import com.example.mycloset.entity.Board;
import com.example.mycloset.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
>>>>>>> 18ec6eafe22bf5af22844ded838986788e92a20f
import java.util.List;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;

    @PostMapping("/create")
<<<<<<< HEAD
    public BoardDTO createBoard(
            @RequestParam String title,
            @RequestParam String text,
            @RequestParam Long userId) {
        return boardService.createBoard(title, text, userId);
    }

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

    @PutMapping("/{id}/like")
    public BoardDTO likeBoard(@PathVariable Long id) {
        return boardService.likeBoard(id);
    }

    @PutMapping("/{id}/unlike")
    public BoardDTO unlikeBoard(@PathVariable Long id) {
        return boardService.unlikeBoard(id);
    }

    @DeleteMapping("/{id}/delete")
    public void deleteBoard(@PathVariable Long id) {
        boardService.deleteBoard(id);
=======
    public Board createBoardWithImages(
            @RequestParam("title") String title,
            @RequestParam("text") String text,
            @RequestParam("userId") Long userId,
            @RequestParam("files") List<MultipartFile> files) throws IOException {
        return boardService.saveBoardWithImages(title, text,userId,files);
>>>>>>> 18ec6eafe22bf5af22844ded838986788e92a20f
    }
}
