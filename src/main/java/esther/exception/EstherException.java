package esther.exception;

/**
 * Represents an error caused by an invalid command entered by the user.
 */
public class EstherException extends Exception {
    /**
     * Creates an exception containing the specified error message.
     *
     * @param message Explanation of the invalid command.
     */
    public EstherException(String message) {
        super(message);
    }
}
