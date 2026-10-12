package duke;

/**
 * Signals an error caused by invalid user input or usage of the chatbot.
 */
public class DuckeException extends Exception {
    /**
     * Creates an exception carrying a user-facing error message.
     *
     * @param message the message describing what went wrong
     */
    public DuckeException(String message) {
        super(message);
    }
}
