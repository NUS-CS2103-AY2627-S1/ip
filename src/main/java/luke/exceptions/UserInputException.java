package luke.exceptions;

/**
 * Represents an input problem that should be shown to the user as a normal
 * chatbot error, rather than treated as a program crash.
 */
public class UserInputException extends Exception {
    /**
     * Creates a user-input exception with the message to display.
     *
     * @param message user-facing error message
     */
    public UserInputException(String message) {
        super(message);
    }
}
