package orion.exception;

/**
 * Represents exceptions specific to the Orion chatbot's execution.
 */
public class OrionException extends RuntimeException {
    /**
     * Initializes a new OrionException with the specified error message.
     *
     * @param message The detail message explaining the error.
     */
    public OrionException(String message) {
        super(message);
    }
}
