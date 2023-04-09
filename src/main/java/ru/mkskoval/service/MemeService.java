package ru.mkskoval.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import ru.mkskoval.dto.MemeResult;
import ru.mkskoval.entity.Meme;
import ru.mkskoval.entity.MemeScore;
import ru.mkskoval.enums.ScoreMemeAction;
import ru.mkskoval.exceptions.MemeScoreActionRepeatException;
import ru.mkskoval.exceptions.UserLikedOwnMemeException;
import ru.mkskoval.repository.MemeRepository;
import ru.mkskoval.repository.MemeScoreRepository;

@Log4j2
@Service
@RequiredArgsConstructor
public class MemeService {

    private final MemeRepository memeRepository;
    private final MemeScoreRepository memeScoreRepository;

    public void saveMeme(Meme meme) {
        memeRepository.save(meme);
    }

    public Meme getMeme(Integer messageId, Long chatId) {
        return memeRepository.findByChatIdAndMessageId(chatId, messageId);
    }

    public void scoreMeme(Long userId, Integer messageId, Long chatId, ScoreMemeAction scoreMemeAction) {
        Meme meme = memeRepository.findByChatIdAndMessageId(chatId, messageId);
        if (meme.getUserId().equals(userId)) {
            log.error("User {} liked his own meme {}", userId, meme.getMemeId());
            throw new UserLikedOwnMemeException();
        }

        MemeScore memeScore = memeScoreRepository.findByUserIdAndMeme(userId, meme);
        if (memeScore != null && memeScore.getScore().equals(scoreMemeAction)) {
            log.error("User {} already {} this meme {}", userId, scoreMemeAction, meme.getMemeId());
            throw new MemeScoreActionRepeatException();
        }

        if (memeScore != null) {
            memeScore.setScore(scoreMemeAction);
            memeScoreRepository.save(memeScore);
        } else {
            MemeScore newScore = new MemeScore();
            newScore.setMeme(meme);
            newScore.setUserId(userId);
            newScore.setScore(scoreMemeAction);
            memeScoreRepository.save(newScore);
        }
    }

    public MemeResult getMemeResult(Meme meme) {
        MemeResult memeResult = new MemeResult();
        memeScoreRepository.findAllByMeme(meme)
                .stream()
                .map(MemeScore::getScore)
                .forEach(memeScore -> {
                    switch (memeScore) {
                        case LIKE -> memeResult.increaseLikes();
                        case DISLIKE -> memeResult.increaseDislikes();
                        case ACCORDION -> memeResult.increaseAccordions();
                    }
                });

        return memeResult;
    }

}
