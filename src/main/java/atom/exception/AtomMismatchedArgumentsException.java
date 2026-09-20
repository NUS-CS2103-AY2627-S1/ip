package atom.exception;

/**
 * An exception for mismatched number of arguments.
 */
public class AtomMismatchedArgumentsException extends AtomException {
    /**
     * Instantiates an <code>AtomMismatchedArgumentsException</code>.
     *
     * @param command The command causing the error.
     * @param expected The expected number of arguments to the command.
     * @param found The number of arguments provided.
     */
    public AtomMismatchedArgumentsException(String command, int expected, int found) {
        super(String.format("Oh no! %s expects %d arguments but got %d.",
                command, expected, found));
    }
}
