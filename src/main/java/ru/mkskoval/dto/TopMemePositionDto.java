package ru.mkskoval.dto;

import lombok.Data;

import javax.persistence.Transient;

@Data
public class TopMemePositionDto {
    private Integer messageId;
    private Long userId;
    private Integer likes;
    private Integer dislikes;
    private Integer accordions;

    public TopMemePositionDto(Integer messageId, Long userId, Integer likes, Integer dislikes, Integer accordions) {
        this.messageId = messageId;
        this.userId = userId;
        this.likes = likes;
        this.dislikes = dislikes;
        this.accordions = accordions;
    }

    @Transient
    private Integer score;

    public void computeScore() {
        this.score = this.likes - this.dislikes;
    }
}
