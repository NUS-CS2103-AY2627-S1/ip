package zaru.exception;

/**
 * Represents errors caused by invalid user commands in the zaru.Zaru chatbot.
 */
public class ZaruException extends Exception {
    /**
     * Creates a zaru.Zaru-specific exception with a message that can be shown to the user.
     *
     * @param message Explanation of what went wrong.
     */
    public ZaruException(String message) {
        super(message);
    }
}
