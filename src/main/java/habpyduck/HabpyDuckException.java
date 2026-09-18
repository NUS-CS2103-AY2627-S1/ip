package habpyduck;

/**
 * Represents an error caused by invalid user input.
 */
public class HabpyDuckException extends Exception {
    /**
     * Creates an exception with a message that can be shown to the user.
     *
     * @param message the explanation of what went wrong.
     */
    public HabpyDuckException(String message) {
        super(message);
    }
}
