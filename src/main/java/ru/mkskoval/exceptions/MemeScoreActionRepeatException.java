package ru.mkskoval.exceptions;

public class MemeScoreActionRepeatException extends IllegalStateException {
    public MemeScoreActionRepeatException() {
        super("Meme score action repeated in meme.");
    }
}
