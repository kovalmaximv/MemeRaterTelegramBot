package ru.mkskoval;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.mkskoval.dto.MemeResult;
import ru.mkskoval.entity.Meme;
import ru.mkskoval.enums.RequestType;
import ru.mkskoval.enums.ScoreMemeAction;
import ru.mkskoval.exceptions.MemeScoreActionRepeatException;
import ru.mkskoval.exceptions.UserLikedOwnMemeException;
import ru.mkskoval.properties.BotConfiguration;
import ru.mkskoval.service.MemeService;
import ru.mkskoval.util.Buttons;

import java.time.LocalDate;

import static ru.mkskoval.util.RequestUtil.handleUpdate;

@Log4j2
@Service
public class Bot extends TelegramLongPollingBot {

    private final MemeService memeService;
    private final BotConfiguration botConfiguration;

    public Bot(MemeService memeService, BotConfiguration botConfiguration) {
        super(botConfiguration.getToken());
        this.memeService = memeService;
        this.botConfiguration = botConfiguration;
    }

    @Override
    public String getBotUsername() {
        return "MemeRaterTelegramBot";
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            log.info("MESSAGE ACCEPTED {}", update);
            RequestType requestType = handleUpdate(update, botConfiguration);

            switch (requestType) {
                case WRONG_CHAT -> sendMessage(update.getMessage().getChatId(), "Отправить мем можно только из мем чата");
                case MEME_SENT -> memeWasSent(update.getMessage());
                case MEME_SCORE -> memeScore(update.getCallbackQuery());
                case UNKNOWN -> log.error("COMMAND UNKNOWN");
                case CHANNEL_MESSAGE -> {}
            }
        } catch (Exception e) {
            log.error("Error was occurred: ", e);
        }
    }

    //------- request handlers zone -------

    private void memeWasSent(Message message) {
        User user = message.getFrom();

        Meme meme = new Meme();
        meme.setChatId(botConfiguration.getMemeChannelId());
        meme.setUserId(user.getId());
        meme.setPublishDate(LocalDate.now());

        Message sentMeme;
        if (message.hasPhoto()) {
            String fileId = message.getPhoto().get(0).getFileId();
            sentMeme = sendMemePhoto(user, fileId);
        } else {
            String fileId = message.getVideo().getFileId();
            sentMeme = sendMemeVideo(user, fileId);
        }

        meme.setMessageId(sentMeme.getMessageId());
        memeService.saveMeme(meme);
        deleteMessage(message.getMessageId(), message.getChatId());
    }

    private void memeScore(CallbackQuery callbackQuery) {
        ScoreMemeAction scoreMemeAction = ScoreMemeAction.byString(callbackQuery.getData());
        User user = callbackQuery.getFrom();
        Message message = callbackQuery.getMessage();

        try {
            memeService.scoreMeme(user.getId(), message.getMessageId(), message.getChatId(), scoreMemeAction);
        } catch (UserLikedOwnMemeException e) {
            sendCallbackAnswer(callbackQuery, "Вы не можете лайкать свой мем.");
            return;
        } catch (MemeScoreActionRepeatException e) {
            String text = String.format("Вы уже поставили %s этому мему.", scoreMemeAction.getEmoji());
            sendCallbackAnswer(callbackQuery, text);
            return;
        }
        Meme meme = memeService.getMeme(message.getMessageId(), message.getChatId());
        MemeResult memeResult = memeService.getMemeResult(meme);
        editKeyboard(message.getMessageId(), message.getChatId(),
                memeResult.getLikes(), memeResult.getDislikes(), memeResult.getAccordions());
        String text = String.format("Вы поставили %s этому мему.", scoreMemeAction.getEmoji());
        sendCallbackAnswer(callbackQuery, text);
    }

    //------- execute actions zone -------

    @SneakyThrows
    private User getChatMember(Long chatId, Long userId) {
        GetChatMember getChatMember = GetChatMember.builder().chatId(chatId).userId(userId).build();

        return execute(getChatMember).getUser();
    }

    @SneakyThrows
    private void sendMessage(Long chatId, String text) {
        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId)
                .parseMode("Markdown")
                .text(text).build();

        execute(sendMessage);
    }

    @SneakyThrows
    private void deleteMessage(Integer messageId, Long chatId) {
        DeleteMessage deleteMessage = DeleteMessage
                .builder()
                .messageId(messageId)
                .chatId(chatId)
                .build();

        execute(deleteMessage);
    }

    private Message sendMemeVideo(User from, String fileId) {
        String caption;
        if (from.getLastName() != null && !from.getLastName().isBlank()) {
            caption = String.format("[От %s %s](tg://user?id=%d)",
                    from.getFirstName(), from.getLastName(), from.getId());
        } else {
            caption = String.format("[От %s](tg://user?id=%d)",
                    from.getFirstName(), from.getId());
        }

        SendVideo msg = SendVideo.builder()
                .chatId(botConfiguration.getMemeChannelId())
                .video(new InputFile(fileId))
                .parseMode("Markdown")
                .caption(caption)
                .replyMarkup(Buttons.getScoreBar(0, 0, 0))
                .build();

        try {
            return execute(msg);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private Message sendMemePhoto(User from, String fileId) {
        String caption;
        if (from.getLastName() != null && !from.getLastName().isBlank()) {
            caption = String.format("[От %s %s](tg://user?id=%d)",
                    from.getFirstName(), from.getLastName(), from.getId());
        } else {
            caption = String.format("[От %s](tg://user?id=%d)",
                    from.getFirstName(), from.getId());
        }

        SendPhoto msg = SendPhoto.builder()
                .chatId(botConfiguration.getMemeChannelId())
                .photo(new InputFile(fileId))
                .parseMode("Markdown")
                .caption(caption)
                .replyMarkup(Buttons.getScoreBar(0, 0, 0))
                .build();

        try {
            return execute(msg);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    private void sendCallbackAnswer(CallbackQuery callbackQuery, String text) {
        AnswerCallbackQuery answerCallbackQuery = AnswerCallbackQuery.builder()
                .callbackQueryId(callbackQuery.getId())
                .text(text)
                .build();

        execute(answerCallbackQuery);
    }

    @SneakyThrows
    private void editKeyboard(Integer messageId, Long chatId, int likes, int dislikes, int accordions) {
        EditMessageReplyMarkup editMessageReplyMarkup = EditMessageReplyMarkup.builder()
                .messageId(messageId)
                .chatId(chatId)
                .replyMarkup(Buttons.getScoreBar(likes, accordions, dislikes))
                .build();

        execute(editMessageReplyMarkup);
    }

}
