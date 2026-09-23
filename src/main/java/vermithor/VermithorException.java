package vermithor;

/**
 * Represents an error caused by an invalid command entered into Vermithor.
 */
public class VermithorException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * Creates an exception with a user-friendly explanation.
     *
     * @param message the explanation shown to the user
     */
    public VermithorException(String message) {
        super(message);
    }
}
