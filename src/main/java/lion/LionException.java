package lion;

/**
 * Signals an invalid command or other user-correctable input problem.
 */
public class LionException extends Exception {
    /**
     * Creates an exception with a message suitable for displaying to the user.
     *
     * @param message explanation of the input problem.
     */
    public LionException(String message) {
        super(message);
    }
}
