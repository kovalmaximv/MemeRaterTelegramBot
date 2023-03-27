package ru.mkskoval;

import org.telegram.telegrambots.meta.api.objects.Update;
import ru.mkskoval.enums.RequestType;

public class RequestUtil {

    public static RequestType handleUpdate(Update update) {
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
