package jason.exception;

/**
 * Indicates that an event command has invalid syntax or date-time data.
 */
public class InvalidEventException extends InvalidCommandException {
    /**
     * Creates an invalid-event exception.
     */
    public InvalidEventException() {
        super();
    }

    /**
     * Returns the expected event command format.
     *
     * @return expected event command format.
     */
    @Override
    public String toString() {
        return "Event commands are in the form \"event {description} /from {YYYY-MM-DD HH:MM} /to {YYYY-MM-DD HH:MM}\"";
    }
}
