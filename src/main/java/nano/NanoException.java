package nano;

/**
 * Represents an exception caused by an invalid Nano command or task.
 */
public class NanoException extends Exception {

    /**
     * Creates a NanoException with the specified error message.
     *
     * @param message error message describing the problem.
     */
    public NanoException(String message) {
        super(message);
    }
}
