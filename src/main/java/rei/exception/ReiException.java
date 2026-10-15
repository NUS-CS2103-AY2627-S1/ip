package rei.exception;

/**
 * Represents an input error that Rei can explain to the user.
 */
public class ReiException extends Exception {
    /**
     * Creates an exception with a user-friendly explanation and correction.
     *
     * @param message Explanation shown in the command-line interface.
     */
    public ReiException(String message) {
        super(message);
    }
}
