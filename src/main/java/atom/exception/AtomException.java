package atom.exception;

/**
 * A parent class for Atom exceptions
 */
public abstract class AtomException extends RuntimeException {
    /**
     * Initializes an <code>AtomException</code>.
     *
     * @param message The error message.
     */
    public AtomException(String message) {
        super(message);
    }
}
