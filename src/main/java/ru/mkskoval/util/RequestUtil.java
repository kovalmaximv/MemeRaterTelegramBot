package ru.mkskoval.util;

import org.telegram.telegrambots.meta.api.objects.Update;
import ru.mkskoval.enums.RequestType;
import ru.mkskoval.properties.BotConfiguration;

public class RequestUtil {

    public static RequestType handleUpdate(Update update, BotConfiguration botConfiguration) {
        if (update.hasMessage() && !update.getMessage().getChatId().equals(botConfiguration.getMemeChatId())) {
            return RequestType.WRONG_CHAT;
        }

        if (update.hasMessage() && update.getMessage().getFrom().getId().equals(777000L)) {
            return RequestType.CHANNEL_MESSAGE;
        }

        if (update.hasMessage() && (update.getMessage().hasPhoto() || update.getMessage().hasVideo())) {
            return RequestType.MEME_SENT;
        }

        if (update.hasCallbackQuery()) {
            return RequestType.MEME_SCORE;
        }

        return RequestType.UNKNOWN;
    }


}
