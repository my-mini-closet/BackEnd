package com.example.mycloset.entity;

import lombok.*;
import java.sql.Timestamp;
import java.util.List;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@ToString
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "boards")
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 300)
    private String text;

    @Builder.Default
    @Column(name = "`like`")
    private Long like = 0L;

    @Builder.Default
    @Column(name = "`unlike`")
    private Long unlike = 0L;

    @ManyToOne
    @JoinColumn(name = "`user`")
    @ToString.Exclude
    private User user;

    @Column(nullable = false)
    @CreationTimestamp
    private Timestamp createdAt;

    @Column(nullable = false)
    @CreationTimestamp
    private Timestamp updatedAt;
    /*
    @Transient
    private List<String> imageUrls;*/
}
