package edith.exception;

/** Represents an application error that Edith can explain to the user. */
public class EdithException extends Exception {
    /**
     * Creates an exception with a message explaining the application error.
     *
     * @param message the user-facing explanation of the application error
     */
    public EdithException(String message) {
        super(message);
    }
}
