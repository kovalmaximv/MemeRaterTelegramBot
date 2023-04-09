package ru.mkskoval.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mkskoval.entity.Meme;

@Repository
public interface MemeRepository extends JpaRepository<Meme, Integer> {
    Meme findByChatIdAndMessageId(Long chatId, Integer messageId);

}
