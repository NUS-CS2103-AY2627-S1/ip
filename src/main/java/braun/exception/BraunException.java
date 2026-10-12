package braun.exception;

/**
 * Represents custom exceptions specific to the Braun chatbot application.
 * Used to signal validation errors, unrecognized broadcast commands, invalid formats, or execution errors.
 */
public class BraunException extends Exception {

    /**
     * Constructs a new {@code BraunException} with the specified detail message.
     *
     * @param message the explanation of the error.
     */
    public BraunException(String message) {
        super(message);
    }

    /**
     * Constructs a new {@code BraunException} with the specified detail message and cause.
     *
     * @param message the explanation of the error.
     * @param cause the underlying cause.
     */
    public BraunException(String message, Throwable cause) {
        super(message, cause);
    }
}
