package dave.exception;

/**
 * Signals that an error occurred while processing a Dave chatbot command.
 */
public class DaveCommandException extends RuntimeException {

    /**
     * Constructs a new DaveCommandException with the specified detail message.
     *
     * @param message Detail message explaining the command failure reason.
     */
    public DaveCommandException(String message) {
        super(message);
    }
}
