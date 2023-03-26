package ru.mkskoval;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.mkskoval.entity.Meme;
import ru.mkskoval.repository.MemeRepository;

import java.time.LocalDate;

public class Bot extends TelegramLongPollingBot {

    private final MemeRepository memeRepository;

    public Bot() {
        super("5829529159:AAFT3H64I6UhLNrHB6P__IGFLD11mazr4No"); // this token was revoked :)
        //super(System.getenv("botToken"));
        memeRepository = new MemeRepository();
    }

    @Override
    public String getBotUsername() {
        return "MemeRaterTelegramBot";
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            if (message.hasPhoto()) {
                memeWasSent(message);
            }
        } else if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            if(callbackQuery.getData().equals("like")) {
                Message message = callbackQuery.getMessage();
                editKeyboard(message.getMessageId(), message.getChatId(), 1, 1, 1);
                sendCallbackAnswer(callbackQuery);
            }
        }
    }

    private void memeWasSent(Message message) {
        User user = message.getFrom();
        Chat chat = message.getChat();
        String fileId  = message.getPhoto().get(0).getFileId();

        Meme meme = new Meme();
        meme.setChatId(chat.getId());
        meme.setMessageId(message.getMessageId());
        meme.setUserId(user.getId());
        meme.setPublishDate(LocalDate.now());

        memeRepository.createMeme(meme);
        sendMeme(chat, user, fileId);
        deleteMessage(message.getMessageId(), message.getChatId());
    }

    private void deleteMessage(Integer messageId, Long chatId) {
        DeleteMessage deleteMessage = DeleteMessage
                .builder()
                .messageId(messageId)
                .chatId(chatId)
                .build();

        try {
            execute(deleteMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendMeme(Chat chat, User from, String fileId) {
        String caption = String.format("[От %s %s](tg://user?id=%d)",
                from.getFirstName(), from.getLastName(), from.getId());

        SendPhoto msg = SendPhoto.builder()
                .chatId(chat.getId())
                .photo(new InputFile(fileId))
                .parseMode("Markdown")
                .caption(caption)
                .replyMarkup(Buttons.getScoreBar(0, 0, 0))
                .build();

        try {
            execute(msg);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendCallbackAnswer(CallbackQuery callbackQuery) {
        AnswerCallbackQuery answerCallbackQuery = AnswerCallbackQuery.builder()
                .callbackQueryId(callbackQuery.getId())
                .text("Liked")
                .build();

        try {
            execute(answerCallbackQuery);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void editKeyboard(Integer messageId, Long chatId, int likes, int dislikes, int accordions) {
        EditMessageReplyMarkup editMessageReplyMarkup = EditMessageReplyMarkup.builder()
                .messageId(messageId)
                .chatId(chatId)
                .replyMarkup(Buttons.getScoreBar(likes, accordions, dislikes))
                .build();

        try {
            execute(editMessageReplyMarkup);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

}
