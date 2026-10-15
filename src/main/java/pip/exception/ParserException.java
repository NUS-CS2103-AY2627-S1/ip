package pip.exception;

/**
 * Indicates that a user command cannot be parsed.
 */
public class ParserException extends Exception {

    /**
     * Creates a ParserException with a message describing the parsing failure.
     *
     * @param message A user-facing description of why the command could not be parsed.
     */
    public ParserException(String message) {
        super(message);
    }
}
