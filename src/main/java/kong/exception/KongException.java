package kong.exception;

/**
 * Represents an error that can be presented to a Kong user.
 */
public class KongException extends Exception {
    /**
     * Creates an exception with a user-facing explanation.
     *
     * @param desc explanation of the error
     */
    public KongException(String desc) {
        super(desc);
    }
}
