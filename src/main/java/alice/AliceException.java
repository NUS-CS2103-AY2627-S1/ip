package alice;

/**
 * Represents an exception specific to the Alice chatbot, thrown when
 * user input is invalid or a command cannot be processed correctly.
 */
public class AliceException extends Exception {
    /**
     * Constructs an AliceException with a message describing the error.
     *
     * @param message description of what went wrong, shown to the user.
     */
    public AliceException(String message) {
        super(message);
    }
}
