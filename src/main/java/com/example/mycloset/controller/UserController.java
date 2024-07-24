package com.example.mycloset.controller;

import com.example.mycloset.dto.UserDTO;
import com.example.mycloset.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    @PostMapping("/updatePersonalColor")
    public UserDTO updatePersonalColor(
            @RequestParam String email,
            @RequestParam String personalColor) throws IOException {
        return userService.updatePersonalColor(email, personalColor);
    }
}
