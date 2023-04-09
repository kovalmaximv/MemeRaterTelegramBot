package ru.mkskoval.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mkskoval.entity.Meme;
import ru.mkskoval.entity.MemeScore;
import ru.mkskoval.entity.MemeScoreID;

import java.util.List;

@Repository
public interface MemeScoreRepository extends JpaRepository<MemeScore, MemeScoreID> {

    List<MemeScore> findAllByMeme(Meme meme);

    MemeScore findByUserIdAndMeme(Long userId, Meme meme);

}
