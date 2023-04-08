package ru.mkskoval;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
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
import ru.mkskoval.dto.TopMemePositionDto;
import ru.mkskoval.entity.Meme;
import ru.mkskoval.enums.RequestType;
import ru.mkskoval.enums.ScoreMemeAction;
import ru.mkskoval.exceptions.MemeScoreActionRepeatException;
import ru.mkskoval.exceptions.UserLikedOwnMemeException;
import ru.mkskoval.service.MemeService;

import java.time.LocalDate;

import static ru.mkskoval.RequestUtil.handleUpdate;

@Log4j2
public class Bot extends TelegramLongPollingBot {

    public static final Long MEME_CHAT_ID = Long.valueOf(System.getProperty("MEME_CHAT_ID"));
    public static final Long MEME_CHANNEL_ID = Long.valueOf(System.getProperty("MEME_CHANNEL_ID"));

    private final MemeService memeService;

    public Bot() {
        super(System.getProperty("BOT_TOKEN"));
        memeService = new MemeService();
        log.info("BOT STARTED");
    }

    @Override
    public String getBotUsername() {
        return "MemeRaterTelegramBot";
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            log.info("MESSAGE ACCEPTED {}", update);
            RequestType requestType = handleUpdate(update);

            switch (requestType) {
                case WRONG_CHAT -> sendMessage(update.getMessage().getChatId(), "Отправить мем можно только из мем чата");
                case MEME_SENT -> memeWasSent(update.getMessage());
                case MEME_SCORE -> memeScore(update.getCallbackQuery());
                case BOT_COMMAND -> botCommand(update.getMessage());
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
        meme.setChatId(MEME_CHANNEL_ID);
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

    private void botCommand(Message message) {
        StringBuilder sb = new StringBuilder();

        LocalDate since;
        if (message.getText().equals("top_memes_day")) {
            since = LocalDate.now().minusDays(1);
        } else {
            since = LocalDate.now().minusDays(7);
        }

        int place = 0;
        for (TopMemePositionDto topMeme: memeService.topMemes(since)) {
            User user = getChatMember(message.getChatId(), topMeme.getUserId());
            String text = String.format("%d. [От %s](https://t.me/c/%s/%s) очков: %d\n",
                    ++place, user.getFirstName(),
                    message.getChatId().toString().substring(4), //weird
                    topMeme.getMessageId(), topMeme.getScore());
            sb.append(text);
        }

        sendMessage(message.getChatId(), sb.toString());
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
                .chatId(MEME_CHANNEL_ID)
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
                .chatId(MEME_CHANNEL_ID)
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
