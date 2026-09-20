package atom.exception;

/**
 * An exception for an invalid task index.
 */
public class AtomTaskNotFoundException extends AtomException {
    /**
     * Initializes an <code>AtomTaskNotFoundException</code>.
     *
     * @param idx The invalid task index provided.
     */
    public AtomTaskNotFoundException(int idx) {
        super(String.format("Oh no! Task %d does not exist.", idx));
    }
}
