package vector;

/**
 * Represents an exception specific to the Vector application.
 */
public class VectorException extends Exception {

    /**
     * Constructs a new VectorException with the given error message.
     *
     * @param message The specific error message.
     */
    public VectorException(String message) {
        super(message);
    }
}
