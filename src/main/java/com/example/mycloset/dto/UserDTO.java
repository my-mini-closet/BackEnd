package com.example.mycloset.dto;

import com.example.mycloset.entity.User;
import lombok.Data;

@Data
public class UserDTO {
    private Long userId;
    private String userEmail;
    private String userNickname;
    private String password;
    private String personalColor;

    static public UserDTO of(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setUserId(user.getId());
        userDTO.setUserEmail(user.getUserEmail());
        userDTO.setUserNickname(user.getNickName());
        userDTO.setPassword(user.getPassword());
        userDTO.setPersonalColor(user.getPersonalColor());
        return userDTO;
    }
}
