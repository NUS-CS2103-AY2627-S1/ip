package caitlyn;

/**
 * Represents an input error that Caitlyn can explain to the user.
 */
public class CaitlynException extends Exception {
    /**
     * Creates an exception with a user-facing explanation.
     *
     * @param message the explanation to display to the user.
     */
    public CaitlynException(String message) {
        super(message);
    }
}
