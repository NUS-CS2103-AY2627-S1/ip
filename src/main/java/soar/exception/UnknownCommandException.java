package soar.exception;

/**
 * Signals that the first word of the user's input is not a supported command.
 */
public class UnknownCommandException extends SoarException {
    /**
     * Creates an exception that lists the commands Soar understands.
     */
    public UnknownCommandException() {
        super("That command is on an unfamiliar flight path. "
                + "Try list, find, date, todo, deadline, event, mark, unmark, delete, edit, or bye "
                + "to keep flying high!");
    }
}
