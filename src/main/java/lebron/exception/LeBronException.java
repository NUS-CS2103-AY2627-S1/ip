package lebron.exception;

/**
 * Represents an exception specific to LeBron, thrown when user input
 * cannot be understood or carried out.
 */
public class LeBronException extends Exception {
    /**
     * Constructs a LeBronException with the given message.
     *
     * @param message Message explaining what went wrong.
     */
    public LeBronException(String message) {
        super(message);
    }
}
