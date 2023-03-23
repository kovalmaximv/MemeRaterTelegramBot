package ru.mkskoval.repository;

import jakarta.persistence.*;
import lombok.extern.slf4j.Slf4j;
import ru.mkskoval.entity.Meme;
import ru.mkskoval.enums.ScoreMemeAction;

@Slf4j
public class MemeRepository {

    @PersistenceContext
    private EntityManager entityManager;


    public void createMeme(Meme meme) {
        entityManager.persist(meme);
        entityManager.flush();
    }

    public void scoreMeme(ScoreMemeAction scoreMemeAction, Long messageId) {
        try {
            TypedQuery<Meme> tq = entityManager.createQuery("from Meme WHERE messageId=?", Meme.class);
            Meme meme = tq.setParameter(1, messageId).getSingleResult();

            switch (scoreMemeAction) {
                case LIKE -> meme.setLikes(meme.getLikes() + 1);
                case DISLIKE -> meme.setDislikes(meme.getDislikes() + 1);
                case ACCORDION -> meme.setAccordions(meme.getAccordions() + 1);
            }

            entityManager.merge(meme);
            entityManager.flush();
        } catch(NoResultException noresult) {
            log.error("Meme not found {}", messageId);
        } catch(NonUniqueResultException notUnique) {
            log.error("Found more than one meme {}", messageId);
        }
    }

}
