package quu.exception;

/**
 * Base class for every error the chatbot expects and can recover from.
 *
 * <p>The main loop catches this one type, so a new failure mode can be added by
 * subclassing it without touching the loop. The message is written for the user to
 * read, not for a developer, and is printed as-is.
 */
public class QuuException extends Exception {

    /**
     * Creates an exception carrying a message meant for the user.
     *
     * @param message the text shown to the user
     */
    public QuuException(String message) {
        super(message);
    }
}
