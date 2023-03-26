package ru.mkskoval.repository;

import lombok.extern.slf4j.Slf4j;
import ru.mkskoval.entity.Meme;
import ru.mkskoval.entity.MemeScore;
import ru.mkskoval.enums.ScoreMemeAction;

import javax.persistence.*;

@Slf4j
public class MemeRepository {

    private static final EntityManagerFactory emf;

    static {
        emf = Persistence.createEntityManagerFactory("pers-unit");
    }

    public static EntityManager getEntityManager() {
        return emf.createEntityManager();
    }


    public void createMeme(Meme meme) {
        EntityManager em = MemeRepository.getEntityManager();
        em.getTransaction().begin();
        em.persist(meme);
        em.getTransaction().commit();
    }

    public void scoreMeme(ScoreMemeAction scoreMemeAction, Long messageId, Long chatId, Long userId) {
        EntityManager em = MemeRepository.getEntityManager();
        try {
            Meme meme = em.createQuery("from Meme WHERE messageId=? AND chatId", Meme.class)
                    .setParameter(1, messageId)
                    .setParameter(2, chatId)
                    .getSingleResult();

            // ToDo if meme score already exist

            MemeScore memeScore = new MemeScore();
            memeScore.setMeme(meme);
            memeScore.setScore(scoreMemeAction);
            memeScore.setUserId(userId);

            em.persist(memeScore);
            em.flush();
        } catch(NoResultException noresult) {
            log.error("Meme not found {}", messageId);
        } catch(NonUniqueResultException notUnique) {
            log.error("Found more than one meme {}", messageId);
        }
    }

}
