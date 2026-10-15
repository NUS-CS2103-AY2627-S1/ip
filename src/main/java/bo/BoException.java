package bo;

/**
 * Represents an input error that can be explained to a Bo user.
 *
 * <p>This is a checked exception so that command-processing methods must
 * either handle an invalid command or pass it back to the main loop.</p>
 */
public class BoException extends Exception {
    /** Keeps serialization of this exception compatible across versions. */
    private static final long serialVersionUID = 1L;

    /**
     * Creates an input error with a user-friendly message.
     *
     * @param message the explanation to show to the user.
     */
    public BoException(String message) {
        super(message);
    }
}
