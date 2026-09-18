package cookie.command;

/** Represents an error caused by invalid input to Cookie. */
public class CookieException extends Exception {

    /**
     * Creates an exception with a message that can be shown to the user.
     *
     * @param message The message describing the error.
     */
    public CookieException(String message) {
        super(message);
    }
}
