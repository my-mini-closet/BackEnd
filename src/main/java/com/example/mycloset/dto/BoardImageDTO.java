package com.example.mycloset.dto;

import com.example.mycloset.entity.BoardImage;
import lombok.Data;
import java.sql.Timestamp;
@Data
public class BoardImageDTO {
    private Long id;
    private String path;
    private String url;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Convert from Entity to DTO
    public static BoardImageDTO fromEntity(BoardImage boardImage) {
        BoardImageDTO dto = new BoardImageDTO();
        dto.setId(boardImage.getId());
        dto.setPath(boardImage.getPath());
        dto.setUrl(boardImage.getUrl());
        dto.setCreatedAt(boardImage.getCreatedAt());
        dto.setUpdatedAt(boardImage.getUpdatedAt());
        return dto;
    }
}
