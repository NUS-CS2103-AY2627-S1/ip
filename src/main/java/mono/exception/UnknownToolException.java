package mono.exception;

/**
 * Indicates that no registered tool matches the requested tool name.
 */
public class UnknownToolException extends MonoException {
    /**
     * Creates an exception for an unrecognized tool name.
     *
     * @param message user-facing explanation of the error
     */
    public UnknownToolException(String message) {
        super(message);
    }
}
