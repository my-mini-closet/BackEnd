package com.example.mycloset.repositoryTest;

import com.example.mycloset.entity.Board;
import com.example.mycloset.entity.User;
import com.example.mycloset.repository.BoardRepository;
import com.example.mycloset.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")  // 테스트 환경에서 "test" 프로파일을 활성화
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY) // 인메모리 데이터 사용
@Sql(scripts = "/schema.sql")  // schema.sql 파일 참조

public class BoardRepositoryTest {

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testFindByUserId() {
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

        List<Board> boards = boardRepository.findByUserId(user.getId());

        assertTrue(boards.size() > 0);
        assertEquals("Test Title", boards.get(0).getTitle());
    }
}
