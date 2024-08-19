package com.example.mycloset.serviceTest;

import com.example.mycloset.dto.BoardDTO;
import com.example.mycloset.entity.Board;
import com.example.mycloset.entity.User;
import com.example.mycloset.repository.BoardRepository;
import com.example.mycloset.repository.UserRepository;
import com.example.mycloset.service.BoardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class BoardServiceTest {

    @Autowired
    private BoardService boardService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Test
    public void testCreateBoard() {
        User user = User.builder()
                .userEmail("test@example.com")
                .nickName("testUser")
                .password("password")
                .build();
        userRepository.save(user);

        String title = "Test Title";
        String text = "Test Text";

        BoardDTO boardDTO = boardService.createBoard(title, text, user.getId());

        assertNotNull(boardDTO);
        assertEquals(title, boardDTO.getTitle());
        assertEquals(text, boardDTO.getText());
        assertEquals(user.getId(), boardDTO.getUserId());
    }

    @Test
    public void testGetAllBoards() {
        User user = User.builder()
                .userEmail("test@example.com")
                .nickName("testUser")
                .password("password")
                .build();
        userRepository.save(user);

        Board board = Board.builder()
                .title("Test Title")
                .text("Test Text")
                .user(user)
                .build();
        boardRepository.save(board);

        List<BoardDTO> boards = boardService.getAllBoards();

        assertFalse(boards.isEmpty());
        assertEquals("Test Title", boards.get(0).getTitle());
    }

    @Test
    public void testLikeBoard() {
        User user = User.builder()
                .userEmail("test@example.com")
                .nickName("testUser")
                .password("password")
                .build();
        userRepository.save(user);

        Board board = Board.builder()
                .title("Test Title")
                .text("Test Text")
                .user(user)
                .like(0L)
                .unlike(0L)
                .build();
        boardRepository.save(board);

        BoardDTO boardDTO = boardService.likeBoard(board.getId(), user.getId());

        assertNotNull(boardDTO);
        //assertEquals(1L, boardDTO.getLike());
    }
}
