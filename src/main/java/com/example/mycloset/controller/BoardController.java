package com.example.mycloset.controller;

import com.example.mycloset.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("")
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;
}
