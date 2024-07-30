package com.example.mycloset.repository;

import com.example.mycloset.entity.Board;
import com.example.mycloset.entity.BoardImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardImageRepository extends JpaRepository<Board, Long> {
    public List<BoardImage> findByBoardId(Long boardId);
    public BoardImage save(BoardImage boardImage);

}