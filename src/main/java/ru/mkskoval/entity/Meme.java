package ru.mkskoval.entity;

import javax.persistence.*;
import lombok.Data;

import java.io.Serial;
import java.time.LocalDate;

@Entity
@Table(
        name="meme",
        uniqueConstraints=@UniqueConstraint(columnNames={"message_id", "chat_id"})
)
@Data
public class Meme {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "meme_id")
    private Integer memeId;

    @Column(nullable = false, name = "message_id")
    private Integer messageId;

    @Column(nullable = false, name = "chat_id")
    private Long chatId;

    @Column(nullable = false, name = "publish_date")
    private LocalDate publishDate;

    @Column(nullable = false, name = "user_id")
    private Long userId;

}
