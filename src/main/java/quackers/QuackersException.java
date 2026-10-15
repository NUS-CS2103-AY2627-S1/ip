package quackers;

/**
 * Represents an error caused by an invalid Quackers command.
 */
public class QuackersException extends Exception {

    /**
     * Creates an exception containing a message suitable for the chatbot UI.
     *
     * @param message the explanation to show the user.
     */
    public QuackersException(String message) {
        super(message);
    }
}
