package jason.exception;

/**
 * Indicates that a todo command has invalid syntax.
 */
public class InvalidToDoException extends InvalidCommandException {
    /**
     * Creates an invalid-todo exception.
     */
    public InvalidToDoException() {
        super();
    }

    /**
     * Returns the expected todo command format.
     *
     * @return expected todo command format.
     */
    @Override
    public String toString() {
        return "Todo commands are in the form \"todo {description} [/for {duration}]\". "
                + "Duration must be between 1 minute and 24 hours, using formats such as \"2h\", "
                + "\"90m\", or \"1h 30m\".";
    }
}
