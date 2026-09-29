package yokohama.exceptions;

/**
 * Represents an error caused by invalid application input or data.
 */
public class YokohamaException extends Exception {
    /**
     * Creates an exception with the specified message.
     *
     * @param message Explanation of the error.
     */
    public YokohamaException(String message) {
        super(message);
    }
}
