package ru.mkskoval.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
@IdClass(MemeID.class)
public class Meme {
    @Id
    private Long messageId;

    @Id
    private Long chatId;

    @Column(nullable = false)
    private Integer likes;

    @Column(nullable = false)
    private Integer dislikes;

    @Column(nullable = false)
    private Integer accordions;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private Long userId;

}
