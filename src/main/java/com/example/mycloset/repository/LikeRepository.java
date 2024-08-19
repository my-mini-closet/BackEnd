package com.example.mycloset.repository;

import com.example.mycloset.entity.Board;
import com.example.mycloset.entity.Like;
import com.example.mycloset.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByUserAndBoard(User user, Board board);
}
