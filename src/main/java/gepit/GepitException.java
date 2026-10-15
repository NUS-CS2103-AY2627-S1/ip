package gepit;

/**
 * Represents an error that occurs while processing Gepit operations.
 */
public class GepitException extends Exception {

    /**
     * Creates a GepitException with the specified error message.
     *
     * @param message Error message describing the problem.
     */
    public GepitException(String message) {
        super(message);
    }
}
