package atom.exception;

/**
 * An exception for invalid argument types.
 */
public class AtomInvalidTypeException extends AtomException {
    /**
     * Initializes an <code>AtomInvalidTypeException</code>.
     *
     * @param command The command raising the exception.
     */
    public AtomInvalidTypeException(String command) {
        super(String.format("Oh no! Invalid argument type for %s", command));
    }
}
