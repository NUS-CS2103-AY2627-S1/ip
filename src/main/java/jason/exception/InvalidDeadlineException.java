package jason.exception;

/**
 * Indicates that a deadline command has invalid syntax or date-time data.
 */
public class InvalidDeadlineException extends InvalidCommandException {
    /**
     * Creates an invalid-deadline exception.
     */
    public InvalidDeadlineException() {
        super();
    }

    /**
     * Returns the expected deadline command format.
     *
     * @return expected deadline command format.
     */
    @Override
    public String toString() {
        return "Deadline commands are in the form \"deadline {description} /by {YYYY-MM-DD HH:MM}\"";
    }
}
