package nova.exception;

/**
 * Represents an exception that occurs while processing Nova commands.
 */
public class NovaException extends Exception {

    /**
     * Creates a NovaException with the given message.
     *
     * @param message error message
     */
    public NovaException(String message) {
        super(message);
    }
}

