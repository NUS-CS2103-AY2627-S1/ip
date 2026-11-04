package soar.exception;

/**
 * Represents an input problem that Soar can explain to the user and recover from.
 */
public class SoarException extends Exception {
    /**
     * Creates an exception with a user-friendly explanation of the input problem.
     *
     * @param message Explanation shown to the user.
     */
    public SoarException(String message) {
        super(message);
    }
}
