package oreo.exception;

/** Represents an anticipated user, data, or environment error in Oreo. */
public class OreoException extends RuntimeException {
    /** Creates an exception with a user-facing explanation of the problem. */
    public OreoException(String message) {
        super(message);
    }
}
