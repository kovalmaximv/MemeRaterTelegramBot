package ru.mkskoval.repository;

import lombok.extern.slf4j.Slf4j;
import ru.mkskoval.dto.TopMemePositionDto;
import ru.mkskoval.entity.Meme;
import ru.mkskoval.entity.MemeScore;
import ru.mkskoval.enums.ScoreMemeAction;

import javax.persistence.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;

@Slf4j
public class MemeRepository {

    private static final EntityManagerFactory emf;

    static {
        emf = Persistence.createEntityManagerFactory("pers-unit");
    }

    public static EntityManager getEntityManager() {
        return emf.createEntityManager();
    }


    public void saveMeme(Meme meme) {
        EntityManager em = MemeRepository.getEntityManager();
        em.getTransaction().begin();
        em.persist(meme);
        em.getTransaction().commit();
    }

    public void saveMemeScore(MemeScore memeScore) {
        EntityManager em = MemeRepository.getEntityManager();
        em.getTransaction().begin();
        em.merge(memeScore);
        em.getTransaction().commit();
    }

    public List<MemeScore> findAllMemeScoreByMeme(Meme meme) {
        EntityManager em = MemeRepository.getEntityManager();
        return em.createQuery("from MemeScore WHERE meme=?1", MemeScore.class)
                .setParameter(1, meme)
                .getResultList();
    }

    // 55, 473099866
    public Meme findMemeByChatAndMessageIds(Integer messageId, Long chatId) {
        EntityManager em = MemeRepository.getEntityManager();

        return em.createQuery("from Meme WHERE messageId=?1 AND chatId=?2", Meme.class)
                .setParameter(1, messageId)
                .setParameter(2, chatId)
                .getSingleResult();
    }

    public MemeScore findMemeScoreByUserAndMeme(Long userId, Meme meme) {
        EntityManager em = MemeRepository.getEntityManager();

        try {
            return em.createQuery("from MemeScore WHERE meme=?1 AND userId=?2", MemeScore.class)
                    .setParameter(1, meme)
                    .setParameter(2, userId)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public List<TopMemePositionDto> topMeme(LocalDate since) {
        EntityManager em = MemeRepository.getEntityManager();
        return em.createNamedQuery("TopMemes", TopMemePositionDto.class)
                .setParameter(1, since)
                .getResultList();
    }

}
