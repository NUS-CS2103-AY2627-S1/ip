package downtowngurl.exception;

/**
 * Represents errors caused by invalid user input in the Downtown Gurl chatbot.
 */
public class DowntownGurlException extends Exception {
    /**
     * Creates an exception with the message that should be shown to the user.
     *
     * @param message User-facing error message.
     */
    public DowntownGurlException(String message) {
        super(message);
    }
}
