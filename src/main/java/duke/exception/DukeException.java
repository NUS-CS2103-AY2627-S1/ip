package duke.exception;

/**
 * Represents an error caused by an invalid user command.
 */
public class DukeException extends Exception {
    /**
     * Creates an exception with a message that can be shown to the user.
     */
    public DukeException(String message) {
        super(message);
    }
}
