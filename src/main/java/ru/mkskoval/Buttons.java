package ru.mkskoval;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

public class Buttons {

    public static InlineKeyboardMarkup getScoreBar(int likes, int accordions, int dislikes) {
        return InlineKeyboardMarkup.builder()
                .keyboardRow(List.of(
                        getLikeButton(likes), getAccordionButton(accordions), getDislikeButton(dislikes)
                )).build();
    }

    private static InlineKeyboardButton getDislikeButton(int dislikes) {
        return InlineKeyboardButton.builder()
                .text(dislikes + " \uD83D\uDC4E")
                .callbackData("dislike")
                .build();
    }

    private static InlineKeyboardButton getLikeButton(int likes) {
        return InlineKeyboardButton.builder()
                .text(likes + " \uD83D\uDC4D")
                .callbackData("like")
                .build();
    }

    private static InlineKeyboardButton getAccordionButton(int accordions) {
        return InlineKeyboardButton.builder()
                .text(accordions + " \uD83E\uDE97")
                .callbackData("accordion")
                .build();
    }

}
