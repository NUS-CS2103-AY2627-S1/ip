package friday;

/**
 * Represents an exception caused by invalid Friday input or storage issues.
 */
public class FridayException extends Exception {

    /**
     * Creates a Friday exception with the given message.
     *
     * @param message explanation of the error
     */
    public FridayException(String message) {
        super(message);
    }
}
