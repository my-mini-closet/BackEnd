package com.example.mycloset.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;


@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Setter
@Getter
@Entity
@Table(name = "images")
public class BoardImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String path;

    @Column(nullable = false, length = 500)
    private String url;

    @ManyToOne
    @JoinColumn(name = "board")
    @ToString.Exclude
    private Board board; // Board 테이블의 외래 키

    @Column(nullable = false)
    @CreationTimestamp
    private Timestamp createdAt;

    @Column(nullable = false)
    @CreationTimestamp
    private Timestamp updatedAt;
}