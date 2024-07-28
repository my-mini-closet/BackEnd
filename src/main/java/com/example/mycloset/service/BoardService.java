package com.example.mycloset.service;

import com.example.mycloset.dto.BoardDTO;
import com.example.mycloset.entity.Board;
import com.example.mycloset.entity.User;
import com.example.mycloset.repository.BoardRepository;
import com.example.mycloset.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;

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
}
