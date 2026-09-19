package eli;

/**
 * Represents an error caused by an invalid command entered into Eli.
 */
public class EliException extends Exception {
    /**
     * Creates an exception with a message that Eli can show to the user.
     *
     * @param message the explanation of the invalid command
     */
    public EliException(String message) {
        super(message);
    }
}
