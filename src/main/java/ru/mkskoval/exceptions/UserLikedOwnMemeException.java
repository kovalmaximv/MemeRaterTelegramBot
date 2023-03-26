package ru.mkskoval.exceptions;

public class UserLikedOwnMemeException extends IllegalStateException {
    public UserLikedOwnMemeException() {
        super("User cannot like own meme.");
    }
}
