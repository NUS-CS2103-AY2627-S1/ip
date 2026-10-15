package sevensix;

/**
 * Represents an error caused by invalid input to SevenSix.
 */
public class SevenSixException extends Exception {
    /**
     * Creates an exception with a message that can be shown to the user.
     *
     * @param message the explanation of the input error.
     */
    public SevenSixException(String message) {
        super(message);
    }
}
