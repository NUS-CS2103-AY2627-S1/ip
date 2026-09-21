package melodie;

/**
 * Represents an error encountered while processing a Melodie command.
 */
public class MelodieException extends Exception {

    /**
     * Creates a Melodie exception with the specified error message.
     *
     * @param message Error message describing what went wrong.
     */
    public MelodieException(String message) {
        super(message);
    }
}
