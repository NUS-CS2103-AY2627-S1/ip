package magnus.exception;

/**
 * Indicates that a recognized command has invalid or incomplete arguments.
 */
public class CommandSyntaxException extends MagnusException {
    /**
     * Creates an exception describing the command syntax error.
     *
     * @param message The detail message describing the error.
     */
    public CommandSyntaxException(String message) {
        super(message);
    }
}
