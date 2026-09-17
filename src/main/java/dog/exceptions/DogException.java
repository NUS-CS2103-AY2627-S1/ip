package dog.exceptions;

/**
 * Custom exception class for the Dog application.
 * Extends Exception to handle application-specific error conditions.
 */
public class DogException extends Exception {
    /**
     * Creates a DogException with the default message "DogException".
     */
    public DogException() {
        super("DogException");
    }

    /**
     * Creates a DogException with the specified message.
     *
     * @param message the detail message.
     */
    public DogException(String message) {
        super(message);
    }
}
