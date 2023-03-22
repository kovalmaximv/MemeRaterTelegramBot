package ru.mkskoval;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

public class Bot extends TelegramLongPollingBot {

    public Bot() {
        super("5829529159:AAHS0GT2GTREevegIUdyPiXjaW7U0im-tcs"); // this token was revoked :)
        //super(System.getenv("botToken"));
    }

    @Override
    public String getBotUsername() {
        return "MemeRaterTelegramBot";
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();

            var user = callbackQuery.getFrom();
            sendText(user.getId(), callbackQuery.getData() + "was pressed");
        } else if (update.hasMessage()) {
            var msg = update.getMessage();
            var user = msg.getFrom();
            var id = user.getId();

            var like = InlineKeyboardButton.builder()
                    .text("\uD83D\uDC4D")
                    .callbackData("like")
                    .build();

            var dislike = InlineKeyboardButton.builder()
                    .text("\uD83D\uDC4E")
                    .callbackData("dislike")
                    .build();

            var keyboard = InlineKeyboardMarkup.builder()
                    .keyboardRow(List.of(like, dislike)).build();
            sendTextWithMenu(id, msg.getText(), keyboard);
        }
    }

    private void sendTextWithMenu(Long who, String what, InlineKeyboardMarkup kb){
        SendMessage sm = SendMessage.builder().chatId(who.toString())
                .parseMode("HTML").text(what)
                .replyMarkup(kb).build();
        try {
            execute(sm);                        //Actually sending the message
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);      //Any error will be printed here
        }
    }

    private void sendText(Long who, String what){
        SendMessage sm = SendMessage.builder().chatId(who.toString())
                .parseMode("HTML").text(what)
                .build();
        try {
            execute(sm);                        //Actually sending the message
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);      //Any error will be printed here
        }
    }

}
