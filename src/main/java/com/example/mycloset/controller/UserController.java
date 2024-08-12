package com.example.mycloset.controller;

import com.example.mycloset.dto.UserDTO;
import com.example.mycloset.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<UserDTO> login(@RequestBody UserDTO userDTO) {
        UserDTO loginuserDTO = userService.login(userDTO.getUserEmail(), userDTO.getPassword());
        if (loginuserDTO != null) {
            return new ResponseEntity<>(loginuserDTO, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }
    @PostMapping("/updatePersonalColor")
    public UserDTO updatePersonalColor(
            @RequestParam String email,
            @RequestParam String personalColor) throws IOException {
        return userService.updatePersonalColor(email, personalColor);
    }

    @PostMapping("/signup")
    public ResponseEntity<UserDTO> signup(@RequestBody UserDTO userDTO) {
        UserDTO createdUser = userService.signup(userDTO.getUserEmail(), userDTO.getUserNickname(), userDTO.getPassword());
        if (createdUser != null) {
            return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
        } else {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }
}
