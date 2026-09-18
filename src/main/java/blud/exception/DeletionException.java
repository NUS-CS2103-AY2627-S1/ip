package blud.exception;

/** Signals that a task deletion request is invalid. */
public class DeletionException extends RuntimeException {
    /** Creates an exception with the default deletion message. */
    public DeletionException() {
        super("Require an integer number to delete");
    }
    /** Creates an exception with a caller-supplied explanation. */
    public DeletionException(String message) {
        super(message);
    }
}
