package com.example.mycloset.service;

<<<<<<< HEAD
import com.example.mycloset.dto.BoardDTO;
import com.example.mycloset.entity.Board;
=======
import com.example.mycloset.entity.Board;
import com.example.mycloset.entity.BoardImage;
>>>>>>> 18ec6eafe22bf5af22844ded838986788e92a20f
import com.example.mycloset.entity.User;
import com.example.mycloset.repository.BoardRepository;
import com.example.mycloset.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

<<<<<<< HEAD
    public BoardDTO createBoard(String title, String text, Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            Board board = Board.builder()
                    .title(title)
                    .text(text)
                    .user(user)
                    .build();
            Board savedBoard = boardRepository.save(board);
            return BoardDTO.fromEntity(savedBoard);
        } else {
            throw new RuntimeException("User not found");
        }
    }

    public List<BoardDTO> getAllBoards() {
        List<Board> boards = boardRepository.findAll();
        return boards.stream().map(BoardDTO::fromEntity).collect(Collectors.toList());
    }

    public BoardDTO getBoardById(Long id) {
        Optional<Board> boardOpt = boardRepository.findById(id);
        if (boardOpt.isPresent()) {
            return BoardDTO.fromEntity(boardOpt.get());
        } else {
            throw new RuntimeException("Board not found");
        }
    }

    public BoardDTO updateBoard(Long id, String title, String text) {
        Optional<Board> boardOpt = boardRepository.findById(id);
        if (boardOpt.isPresent()) {
            Board board = boardOpt.get();
            board.setTitle(title);
            board.setText(text);
            Board updatedBoard = boardRepository.save(board);
            return BoardDTO.fromEntity(updatedBoard);
        } else {
            throw new RuntimeException("Board not found");
        }
    }

    public BoardDTO likeBoard(Long id) {
        Optional<Board> boardOpt = boardRepository.findById(id);
        if (boardOpt.isPresent()) {
            Board board = boardOpt.get();
            board.setLike(board.getLike() + 1);
            Board updatedBoard = boardRepository.save(board);
            return BoardDTO.fromEntity(updatedBoard);
        } else {
            throw new RuntimeException("Board not found");
        }
    }

    public BoardDTO unlikeBoard(Long id) {
        Optional<Board> boardOpt = boardRepository.findById(id);
        if (boardOpt.isPresent()) {
            Board board = boardOpt.get();
            board.setUnlike(board.getUnlike() + 1);
            Board updatedBoard = boardRepository.save(board);
            return BoardDTO.fromEntity(updatedBoard);
        } else {
            throw new RuntimeException("Board not found");
        }
    }

    public void deleteBoard(Long id) {
        boardRepository.deleteById(id);
    }
=======
    // application.yaml 파일에서 설정한 값을 가져온다
    @Value("${image.upload.dir}")

    private String uploadDir;

    @Value("${image.access.url}")
    private String accessUrl;


    // @Transactional(readOnly = true)


    public Board saveBoardWithImages(String title, String text, Long userId, List<MultipartFile> files) throws IOException {
        // userId를 이용하여 user 테이블의 해당 사용자 정보를 가져온다
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Board board = Board.builder()
                .title(title)
                .text(text)
                .user(user)
                .build();

        // 실제 경로 가져오기
        File staticImageDir = new ClassPathResource("static/images").getFile();
        String realPath = staticImageDir.getAbsolutePath();

        List<BoardImage> images = new ArrayList<>();
        for (MultipartFile file : files) {
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            String filePath = realPath + File.separator + fileName;
            String fileUrl = accessUrl + "/" + fileName;

            File dest = new File(filePath);
            file.transferTo(dest);

            images.add(BoardImage.builder()
                    .url(fileUrl)
                    .path(filePath)
                    .board(board)
                    .build());
        }
        // Board와 Image의 관계 설정, board에 이미지 리스트를 저장
        board.setImages(images);

        return boardRepository.save(board);
    }

>>>>>>> 18ec6eafe22bf5af22844ded838986788e92a20f
}
