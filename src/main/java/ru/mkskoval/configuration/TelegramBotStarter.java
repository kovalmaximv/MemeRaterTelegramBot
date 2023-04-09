package ru.mkskoval.configuration;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.mkskoval.Bot;

@Component
@Log4j2
public class TelegramBotStarter {

    @Autowired
    private Bot bot;

    @EventListener({ApplicationReadyEvent.class})
    public void init() {
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(bot);
            log.info("BOT STARTED");
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }
    }
}
