package com.example.mycloset.service;

import com.example.mycloset.dto.BoardDTO;
import com.example.mycloset.dto.BoardImageDTO;
import com.example.mycloset.entity.Board;
import com.example.mycloset.entity.BoardImage;
import com.example.mycloset.entity.User;
import com.example.mycloset.repository.BoardRepository;
import com.example.mycloset.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

    public BoardDTO createBoard(String title, String text, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Board board = Board.builder()
                .title(title)
                .text(text)
                .user(user)
                .build();
        Board savedBoard = boardRepository.save(board);
        return BoardDTO.fromEntity(savedBoard);
    }
    @Transactional
    public List<BoardDTO> getAllBoards() {
        return boardRepository.findAll().stream()
                .map(BoardDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public BoardDTO getBoardById(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Board not found"));
        return BoardDTO.fromEntity(board);
    }

    public BoardDTO updateBoard(Long id, String title, String text) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Board not found"));
        board.setTitle(title);
        board.setText(text);
        Board updatedBoard = boardRepository.save(board);
        return BoardDTO.fromEntity(updatedBoard);
    }

    public BoardDTO likeBoard(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Board not found"));
        board.setLike(board.getLike() + 1);
        Board updatedBoard = boardRepository.save(board);
        return BoardDTO.fromEntity(updatedBoard);
    }

    public BoardDTO unlikeBoard(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Board not found"));
        board.setUnlike(board.getUnlike() + 1);
        Board updatedBoard = boardRepository.save(board);
        return BoardDTO.fromEntity(updatedBoard);
    }

    @Value("${image.upload.dir}")
    private String uploadDir;

    @Value("${image.access.url}")
    private String accessUrl;

    public BoardDTO saveBoardWithImages(String title, String text, Long userId, List<MultipartFile> files) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Board board = Board.builder()
                .title(title)
                .text(text)
                .user(user)
                .build();

        // 절대 경로를 사용하여 파일을 저장
        String realPath = uploadDir;

        List<BoardImage> images = new ArrayList<>();
        for (MultipartFile file : files) {
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            String filePath = realPath + File.separator + fileName;
            String fileUrl = accessUrl + "/" + fileName;

            System.out.println("Attempting to save file to: " + filePath);

            File dest = new File(filePath);
            // 폴더가 존재하지 않으면 생성
            if (!dest.getParentFile().exists()) {
                dest.getParentFile().mkdirs();
            }
            // 파일 저장
            file.transferTo(dest);

            images.add(BoardImage.builder()
                    .url(fileUrl)
                    .path(filePath)
                    .board(board)
                    .build());
        }

        board.setImages(images);
        Board savedBoard = boardRepository.save(board);
        return BoardDTO.fromEntity(savedBoard);
    }

    public void deleteBoard(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Board not found"));

        for (BoardImage image : board.getImages()) {
            File file = new File(image.getPath());
            if (file.exists()) {
                file.delete();
            }
        }
        boardRepository.deleteById(id);
    }

    public Page<BoardDTO> getBoardsByPage(int page, int pageSize) {
        PageRequest pageRequest = PageRequest.of(page, pageSize, Sort.by("createdAt").descending());
        return boardRepository.findAll(pageRequest).map(BoardDTO::fromEntity);
    }
}
