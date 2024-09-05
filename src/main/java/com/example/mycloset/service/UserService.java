package com.example.mycloset.service;

import com.example.mycloset.dto.UserDTO;
import com.example.mycloset.entity.User;
import com.example.mycloset.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    // 로그인 유효성 검사 메서드
    public UserDTO login(String email, String password) {
        Optional<User> userOpt = userRepository.findByUserEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // 비밀번호 검사
            if (user.getPassword().equals(password)) {
                return UserDTO.of(user);
            }
        }
        return null; // 유효하지 않은 경우 null 반환
    }
    public UserDTO updatePersonalColor(Long userId, String personalColor) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User updateUser = userOpt.get();
            updateUser.setPersonalColor(personalColor);
            User savedUser = userRepository.save(updateUser);
            return UserDTO.of(savedUser);
        } else {
            return null;
        }
    }
    public UserDTO signup(String email, String nickname, String password) {
        Optional<User> userOpt = userRepository.findByUserEmail(email);
        if (userOpt.isPresent()) {
            return null;
        } else {
            User newUser = User.builder()
                    .userEmail(email)
                    .nickName(nickname)
                    .password(password)
                    .build();
            User savedUser = userRepository.save(newUser);
            return UserDTO.of(savedUser);
        }
    }
}
