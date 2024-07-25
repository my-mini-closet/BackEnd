package com.example.mycloset.controller;

import com.example.mycloset.entity.Board;
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

    @PostMapping("/create")
    public Board createBoardWithImages(
            @RequestParam("title") String title,
            @RequestParam("text") String text,
            @RequestParam("userId") Long userId,
            @RequestParam("files") List<MultipartFile> files) throws IOException {
        return boardService.saveBoardWithImages(title, text,userId,files);
    }
}
