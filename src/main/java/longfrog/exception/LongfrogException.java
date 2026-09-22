package longfrog.exception;

/**
 * Represents an input error that can be shown in the Longfrog console without ending the session.
 */
public class LongfrogException extends Exception {
    /**
     * Creates an exception with the user-facing explanation of the input error.
     *
     * @param message the explanation to display in the console
     */
    public LongfrogException(String message) {
        super(message);
    }
}
