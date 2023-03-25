package ru.mkskoval.entity;

import jakarta.persistence.*;
import lombok.Data;
import ru.mkskoval.enums.ScoreMemeAction;

@Entity
@Data
@IdClass(MemeScoreID.class)
public class MemeScore {
    @Id
    @ManyToOne
    @JoinColumn(name = "meme_id", referencedColumnName = "message_id")
    private Meme meme;

    @Id
    private Long userId;

    @Enumerated
    @Column(nullable = false)
    private ScoreMemeAction score;

}
