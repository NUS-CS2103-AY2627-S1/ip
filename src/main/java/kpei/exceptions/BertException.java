package kpei.exceptions;

/**
 * Represents the base exception for application-specific errors in bert.CommandCenter.
 */
public class BertException extends Exception {
    /**
     * Constructs a {@code BertException} with the specified detail message.
     *
     * @param message The detail message describing the error.
     */
    public BertException(String message) {
        super(message);
    }

    /**
     * Constructs a {@code BertException} with the specified detail message and cause.
     *
     * @param message The detail message describing the error.
     * @param cause The underlying cause of the exception.
     */
    public BertException(String message, Throwable cause) {
        super(message, cause);
    }
}
