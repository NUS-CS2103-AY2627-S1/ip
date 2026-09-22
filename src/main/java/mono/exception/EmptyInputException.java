package mono.exception;

/**
 * Indicates that the user submitted an empty input line.
 */
public class EmptyInputException extends MonoException {
    /**
     * Creates an exception for an input containing no command.
     *
     * @param message user-facing explanation of the error
     */
    public EmptyInputException(String message) {
        super(message);
    }
}
