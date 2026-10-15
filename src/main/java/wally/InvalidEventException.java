package wally;

/**
 * Represents an event task with an invalid format or time range.
 */
public class InvalidEventException extends Exception {
    /** Creates an error explaining the expected event command format. */
    public InvalidEventException() {
        super("Format: event <name> /from <date: yyyy-MM-dd> <time: HH:mm>"
                + " /to <date: yyyy-MM-dd> <time: HH:mm>");
    }

    /** Creates an error with a specific event validation message. */
    public InvalidEventException(String message) {
        super(message);
    }
}
