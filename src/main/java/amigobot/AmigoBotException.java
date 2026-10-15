package amigobot;

/**
 * Represents an exception specific to AmigoBot.
 * Thrown when the user provides invalid input or an unrecognized command.
 */
public class AmigoBotException extends Exception {
    /**
     * Constructs an AmigoBotException with the given error message.
     *
     * @param message the error message describing what went wrong.
     */
    public AmigoBotException(String message) {
        super(message);
    }
}
