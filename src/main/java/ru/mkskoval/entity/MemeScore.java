package ru.mkskoval.entity;

import jakarta.persistence.*;
import lombok.Data;
import ru.mkskoval.enums.ScoreMemeAction;

@Entity
@Table(name="meme_score")
@IdClass(MemeScoreID.class)
@Data
public class MemeScore {
    @Id
    @ManyToOne
    @JoinColumn(name = "meme_id", referencedColumnName = "meme_id")
    private Meme meme;

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private ScoreMemeAction score;

}
