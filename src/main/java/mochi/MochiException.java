package mochi;

/**
 * Represents an error caused by an invalid Mochi command.
 */
public class MochiException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * Creates an exception with a user-facing explanation of the error.
     *
     * @param message Explanation of the invalid command.
     */
    public MochiException(String message) {
        super(message);
    }

    /**
     * Returns the error in Mochi's user-facing message format.
     *
     * @return Formatted error message.
     */
    @Override
    public String toString() {
        return "OOPS!!! " + getMessage();
    }
}
