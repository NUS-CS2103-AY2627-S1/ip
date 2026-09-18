package chatbot;

/**
 * An exception thrown when a user provides invalid input.
 */
public class InvalidInputException extends RuntimeException {

    /**
     * Creates an InvalidInputException with given message.
     *
     * @param message Message to be printed.
     */
    public InvalidInputException(String message) {
        super(message);
    }
}
