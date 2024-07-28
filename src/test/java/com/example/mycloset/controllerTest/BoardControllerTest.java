package com.example.mycloset.controllerTest;

import com.example.mycloset.dto.BoardDTO;
import com.example.mycloset.entity.User;
import com.example.mycloset.repository.UserRepository;
import com.example.mycloset.service.BoardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class BoardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BoardService boardService;

    @Test
    @WithMockUser
    public void testCreateBoard() throws Exception {
        User user = User.builder()
                .userEmail("test@example.com")
                .nickName("testUser")
                .password("password")
                .build();
        userRepository.save(user);

        mockMvc.perform(post("/api/boards/create")
                        .param("title", "Test Title")
                        .param("text", "Test Text")
                        .param("userId", String.valueOf(user.getId()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Title"))
                .andExpect(jsonPath("$.text").value("Test Text"))
                .andExpect(jsonPath("$.userId").value(user.getId()));
    }

    @Test
    @WithMockUser
    public void testGetAllBoards() throws Exception {
        User user = User.builder()
                .userEmail("test@example.com")
                .nickName("testUser")
                .password("password")
                .build();
        userRepository.save(user);

        BoardDTO boardDTO = new BoardDTO();
        boardDTO.setTitle("Test Title");
        boardDTO.setText("Test Text");
        boardDTO.setUserId(user.getId());

        boardService.createBoard(boardDTO.getTitle(), boardDTO.getText(), user.getId());

        mockMvc.perform(get("/api/boards/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Test Title"))
                .andExpect(jsonPath("$[0].text").value("Test Text"))
                .andExpect(jsonPath("$[0].userId").value(user.getId()));
    }
}
