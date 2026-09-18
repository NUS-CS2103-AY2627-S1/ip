package phin;

/**
 * Represents an invalid command or input that Phin can explain to the user.
 */
public class PhinException extends Exception {
    /**
     * Creates an exception with a user-facing explanation of the error.
     *
     * @param message explanation that tells the user how to correct the input.
     */
    public PhinException(String message) {
        super(message);
    }
}
