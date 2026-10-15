package trybot.exception;

/**
 * Represents an input or task-management error reported by TryBot.
 */
public class TryBotException extends Exception {

    /**
     * Creates an exception with a user-friendly explanation of the error.
     *
     * @param message explanation shown to the user.
     */
    public TryBotException(String message) {
        super(message);
    }
}
