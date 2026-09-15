package ekud;

/**
 * A user-facing error, such as empty input or extra arguments on a command.
 * These are shown to the user instead of crashing the chatbot.
 */
public class EkudException extends Exception {
    /**
     * Creates an exception with a message suitable to display to the user.
     *
     * @param message explanation of what went wrong
     */
    public EkudException(String message) {
        super(message);
    }
}
