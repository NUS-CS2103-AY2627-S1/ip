package atom.exception;

/**
 * An exception for invalid commands.
 */
public class AtomInvalidCommandException extends AtomException {
    /**
     * Initializes an <code>AtomInvalidCommandException</code>.
     */
    public AtomInvalidCommandException() {
        super("Oh no! I don't know what that means.");
    }
}
