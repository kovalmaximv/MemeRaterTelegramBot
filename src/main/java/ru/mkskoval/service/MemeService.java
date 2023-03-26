package ru.mkskoval.service;

import lombok.extern.slf4j.Slf4j;
import ru.mkskoval.dto.MemeResult;
import ru.mkskoval.entity.Meme;
import ru.mkskoval.entity.MemeScore;
import ru.mkskoval.enums.ScoreMemeAction;
import ru.mkskoval.exceptions.MemeScoreActionRepeatException;
import ru.mkskoval.exceptions.UserLikedOwnMemeException;
import ru.mkskoval.repository.MemeRepository;

@Slf4j
public class MemeService {

    private final MemeRepository memeRepository;

    public MemeService() {
        this.memeRepository = new MemeRepository();
    }

    public void saveMeme(Meme meme) {
        memeRepository.saveMeme(meme);
    }

    public Meme getMeme(Integer messageId, Long chatId) {
        return memeRepository.findMemeByChatAndMessageIds(messageId, chatId);
    }

    public void scoreMeme(Long userId, Integer messageId, Long chatId, ScoreMemeAction scoreMemeAction) {
        Meme meme = memeRepository.findMemeByChatAndMessageIds(messageId, chatId);
        /*if (meme.getUserId().equals(userId)) {
            log.error("User {} liked his own meme {}", userId, meme.getMemeId());
            throw new UserLikedOwnMemeException();
        }*/

        MemeScore memeScore = memeRepository.findMemeScoreByUserAndMeme(userId, meme);
        if (memeScore != null && memeScore.getScore().equals(scoreMemeAction)) {
            log.error("User {} already {} this meme {}", userId, scoreMemeAction, meme.getMemeId());
            throw new MemeScoreActionRepeatException();
        }

        if (memeScore != null) {
            memeScore.setScore(scoreMemeAction);
            memeRepository.saveMemeScore(memeScore);
        } else {
            MemeScore newScore = new MemeScore();
            newScore.setMeme(meme);
            newScore.setUserId(userId);
            newScore.setScore(scoreMemeAction);
            memeRepository.saveMemeScore(newScore);
        }
    }

    public MemeResult getMemeResult(Meme meme) {
        MemeResult memeResult = new MemeResult();
        memeRepository.findAllMemeScoreByMeme(meme)
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
