package jaku;

/**
 * Represents an expected problem with a command entered by the user.
 */
public class JakuException extends Exception {
    /** Serialization identifier for this exception type. */
    private static final long serialVersionUID = 1L;

    /**
     * Creates an exception containing the guidance that should be shown to the user.
     *
     * @param message explanation of the invalid command and how to correct it
     */
    public JakuException(String message) {
        super(message);
    }
}
