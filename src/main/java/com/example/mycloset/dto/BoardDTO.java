package com.example.mycloset.dto;

import com.example.mycloset.entity.Board;
//import com.example.mycloset.entity.Image;
import com.example.mycloset.entity.BoardImage;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;
// 순수하게 데이터만 주고 받기 위해 가벼운 모듈
@Data // getter, setter만 존재
public class BoardDTO {
    private Long id;
    private String title;
    private String text;
    private Long likeCount;
    private Long unlikeCount;
    private List<String> imageUrls;
    private Long userId;
    //private String userEmail;
    private String userNickName;
    // getters and setters

    public static BoardDTO fromEntity(Board board) {
        BoardDTO dto = new BoardDTO();
        dto.setId(board.getId());
        dto.setTitle(board.getTitle());
        dto.setText(board.getText());
        dto.setLikeCount(board.getLike());
        dto.setUnlikeCount(board.getUnlike());
        dto.setUserId(board.getUser().getId());
        //dto.setUserEmail(board.getUser().getUserEmail());
        dto.setUserNickName(board.getUser().getNickName());
        dto.setImageUrls(board.getImages().stream().map(BoardImage::getUrl).collect(Collectors.toList()));
        return dto;
    }
}