package ru.mkskoval.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ScoreMemeAction {
    LIKE("\uD83D\uDC4D"),
    DISLIKE("\uD83D\uDC4E"),
    ACCORDION("\uD83E\uDE97");

    private final String emoji;

    public static ScoreMemeAction byString(String action) {
        return ScoreMemeAction.valueOf(action.toUpperCase());
    }
}
