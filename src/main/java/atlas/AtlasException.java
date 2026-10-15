package atlas;

/**
 * Represents an error caused by invalid user input to Atlas.
 */
public class AtlasException extends Exception {
    /** Keeps serialized AtlasException instances compatible across versions. */
    private static final long serialVersionUID = 1L;

    /**
     * Creates an exception describing an invalid Atlas operation.
     *
     * @param message explanation of the error shown to the user.
     */
    public AtlasException(String message) {
        super(message);
    }
}
