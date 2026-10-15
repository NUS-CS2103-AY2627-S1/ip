package larry.exception;

/**
 * Represents an invalid command or input that Larry can report to the user.
 */
public class LarryException extends Exception {
    private static final String ERROR_PREFIX = "ERROR: ";
    private static final String UNKNOWN_COMMAND_MESSAGE = "EVIL LARRY rejects that command. "
            + "Use todo, deadline, event, list, mark, unmark, delete, edit, find, on, or bye.";

    /**
     * Creates a Larry-specific exception with the chatbot's standard error message.
     */
    public LarryException() {
        this(UNKNOWN_COMMAND_MESSAGE);
    }

    /**
     * Creates a Larry-specific exception with an actionable, in-character explanation.
     *
     * @param message Explanation of the invalid input.
     */
    public LarryException(String message) {
        super(ERROR_PREFIX + message);
    }
}
