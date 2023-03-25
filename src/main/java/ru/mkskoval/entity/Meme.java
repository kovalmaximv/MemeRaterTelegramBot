package ru.mkskoval.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@IdClass(MemeID.class)
@Table(
        name="meme",
        uniqueConstraints=
        @UniqueConstraint(columnNames={"messageId", "chatId"})
)
@Data
public class Meme {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long memeId;

    @Column(nullable = false)
    private Integer messageId;

    @Column(nullable = false)
    private Long chatId;

    //@Column(nullable = false)
    //private LocalDate publishDate;

    @Column(nullable = false)
    private Long userId;

}
