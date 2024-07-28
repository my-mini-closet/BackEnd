package com.example.mycloset.repositoryTest;

import com.example.mycloset.entity.User;
import com.example.mycloset.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@ActiveProfiles("test")  // 테스트 환경에서 "test" 프로파일을 활성화
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY) // 인메모리 데이터 사용
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    public void testFindByUserEmail() {
        User user = User.builder()
                .userEmail("test@example.com")
                .nickName("testUser")
                .password("password")
                .build();
        userRepository.save(user);
        entityManager.persistAndFlush(user);  // TestEntityManager를 사용하여 엔티티 저장

        Optional<User> foundUser = userRepository.findByUserEmail("test@example.com");

        assertTrue(foundUser.isPresent());
        assertEquals("testUser", foundUser.get().getNickName());
    }
}
