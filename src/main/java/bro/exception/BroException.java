package bro.exception;

/**
 * Custom exception thrown for errors specific to the Bro chatbot application.
 */
public class BroException extends Exception {
    /**
     * Constructs a BroException with the specified error message.
     *
     * @param message The message explaining the cause of the exception.
     */
    public BroException(String message) {
        super(message);
    }
}
