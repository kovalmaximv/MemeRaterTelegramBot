package ru.mkskoval;

import org.telegram.telegrambots.meta.api.objects.Update;
import ru.mkskoval.enums.RequestType;

import static ru.mkskoval.Bot.MEME_CHAT_ID;

public class RequestUtil {

    public static RequestType handleUpdate(Update update) {
        if (update.hasMessage() && !update.getMessage().getChatId().equals(MEME_CHAT_ID)) {
            return RequestType.WRONG_CHAT;
        }

        if (update.hasMessage() && update.getMessage().getFrom().getId().equals(777000L)) {
            return RequestType.CHANNEL_MESSAGE;
        }

        if (update.hasMessage() && update.getMessage().hasPhoto()) {
            return RequestType.MEME_SENT;
        }

        if (update.hasMessage() && update.getMessage().isCommand()) {
            return RequestType.BOT_COMMAND;
        }

        if (update.hasCallbackQuery()) {
            return RequestType.MEME_SCORE;
        }

        return RequestType.UNKNOWN;
    }


}
