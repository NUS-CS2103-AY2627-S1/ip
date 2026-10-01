package bany.exceptions;

/** Base checked exception for Bany-specific failures. */
public class BanyException extends Exception {
    /**
     * Creates a Bany exception with an explanation of the failure.
     *
     * @param message explanation of the failure.
     */
    public BanyException(String message) {
        super(message);
    }
}
