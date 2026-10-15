package olaf;

/**
 * Signals that a user command cannot be understood or does not follow the required syntax.
 */
final class CommandParseException extends Exception {
    private static final long serialVersionUID = 1L;

    CommandParseException(String message) {
        super(message);
    }
}
