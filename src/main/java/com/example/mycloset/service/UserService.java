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

    public UserDTO updatePersonalColor(String email, String personalColor) {
        Optional<User> userOpt = userRepository.findByUserEmail(email);
        if (userOpt.isPresent()) {
            User updateUser = userOpt.get();
            updateUser.setPersonalColor(personalColor);
            User savedUser = userRepository.save(updateUser);
            return UserDTO.of(savedUser);
        } else {
            return null;
        }
    }
}
