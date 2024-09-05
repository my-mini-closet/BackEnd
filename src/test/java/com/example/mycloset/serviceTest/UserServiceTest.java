package com.example.mycloset.serviceTest;

import com.example.mycloset.dto.UserDTO;
import com.example.mycloset.entity.User;
import com.example.mycloset.repository.UserRepository;
import com.example.mycloset.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Test
    public void testSignup() {
        String email = "test@example.com";
        String nickname = "testUser";
        String password = "password";

        UserDTO userDTO = userService.signup(email, nickname, password);

        assertNotNull(userDTO);
        assertEquals(email, userDTO.getUserEmail());
        assertEquals(nickname, userDTO.getUserNickname());
    }

    @Test
    public void testSignupExistingEmail() {
        String email = "test@example.com";
        String nickname = "testUser";
        String password = "password";

        userService.signup(email, nickname, password); // First signup

        UserDTO userDTO = userService.signup(email, nickname, password); // Second signup with same email

        assertNull(userDTO); // Should return null for existing email
    }

    @Test
    public void testUpdatePersonalColor() {
        String email = "test@example.com";
        String nickname = "testUser";
        String password = "password";
        String personalColor = "blue";
        Long id = 1L;
        userService.signup(email, nickname, password); // First signup

        UserDTO updatedUserDTO = userService.updatePersonalColor(id, personalColor);

        assertNotNull(updatedUserDTO);
        assertEquals(personalColor, updatedUserDTO.getPersonalColor());
    }
}
