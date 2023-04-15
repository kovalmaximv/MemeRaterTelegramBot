package ru.mkskoval;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.mkskoval.properties.BotConfiguration;

@Log4j2
@Service
public class Bot extends TelegramLongPollingBot {

    public Bot(BotConfiguration botConfiguration) {
        super(botConfiguration.getToken());
    }

    @Override
    public String getBotUsername() {
        return "TelegramBot";
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            log.info("MESSAGE ACCEPTED {}", update);
            // Do something
        } catch (Exception e) {
            log.error("Error was occurred: ", e);
        }
    }

}
