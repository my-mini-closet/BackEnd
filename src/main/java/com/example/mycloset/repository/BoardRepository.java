package com.example.mycloset.repository;

import com.example.mycloset.entity.Board;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
    public List<Board> findAll();
    public List<Board> findByUserId(Long userId);
    public List<Board> findByUserUserEmail(String userEmail);
    public Board save(Board board);
    public List<Board> findByUserId(Long id, Sort createdAt);
    public void deleteById(Long id);
}