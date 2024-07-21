package com.example.mycloset.entity;

import com.example.mycloset.dto.UserDTO;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.bytecode.enhance.spi.EnhancementInfo;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String userEmail;

    @Column(length = 200)
    private String password;

    @Column(nullable = false, length = 200)
    private String nickName;

    @Column(nullable = false)
    @CreationTimestamp
    private Timestamp createdAt;

    @Column(nullable = false)
    @CreationTimestamp
    private Timestamp updatedAt;

    @Column(length = 200)
    private String personalColor;

    public static User fromUserDTO(UserDTO userDTO) {
        return User.builder()
                .id(userDTO.getUserId())
                .userEmail(userDTO.getUserEmail())
                .nickName(userDTO.getUserNickname())
                .build();
    }
}