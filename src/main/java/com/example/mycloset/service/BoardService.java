package com.example.mycloset.service;

import com.example.mycloset.entity.Board;
import com.example.mycloset.entity.BoardImage;
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

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

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

}
