package recordbase.exceptions;

/**
 * Represents an exception that occurs when an error is encountered while processing Record operations.
 */

public class RecordException extends RuntimeException {

    /**
     * Constructs a {@code RecordException} with the specified error message.
     *
     * @param errorMessage the message describing the cause of exception
     */
    public RecordException(String errorMessage) {
        super(errorMessage);
    }

    /**
     * Constructs a {@code RecordException} with the specified error message and underlying cause.
     *
     * @param errorMessage the message describing the cause of exception
     * @param cause the underlying cause of the exception
     */
    public RecordException(String errorMessage, Throwable cause) {
        super(errorMessage, cause);
    }
}
