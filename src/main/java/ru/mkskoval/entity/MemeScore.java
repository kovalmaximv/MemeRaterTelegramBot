package ru.mkskoval.entity;

import javax.persistence.*;
import lombok.Data;
import ru.mkskoval.dto.TopMemePositionDto;
import ru.mkskoval.enums.ScoreMemeAction;

@Entity
@Table(name="meme_score")
@IdClass(MemeScoreID.class)
@Data
@NamedNativeQuery(name = "TopMemes", query =
        """
        SELECT * FROM(
            SELECT m.message_id AS messageId, m.user_id AS userId,
                sum(case when ms.score = 'LIKE' then 1 else 0 end) AS likes,
                sum(case when ms.score = 'DISLIKE' then 1 else 0 end) AS dislikes,
                sum(case when ms.score = 'ACCORDION' then 1 else 0 end) AS accordions
            FROM meme_score ms
                INNER JOIN meme m ON ms.meme_id=m.meme_id
            WHERE m.publish_date > ?
            GROUP BY m.message_id, m.user_id
        ) AS tm
        WHERE tm.likes > tm.dislikes
        ORDER BY (likes - dislikes) DESC;
        """,
        resultSetMapping = "TopMemeMapping")
@SqlResultSetMapping(name = "TopMemeMapping",
        classes = {
                @ConstructorResult(
                        columns = {
                                @ColumnResult(name = "messageId", type = Integer.class),
                                @ColumnResult(name = "userId", type = Long.class),
                                @ColumnResult(name = "likes", type = Integer.class),
                                @ColumnResult(name = "dislikes", type = Integer.class),
                                @ColumnResult(name = "accordions", type = Integer.class)
                        },
                        targetClass = TopMemePositionDto.class
                )}
)
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
