package noms.exception;

/**
 * Indicates that Noms received an empty command.
 */
public class EmptyCommandException extends NomsException {
    /** Creates the exception with a message listing the valid commands. */
    public EmptyCommandException() {
        super("Noms needs a command. Try feeding me a todo, deadline, event, list, "
                + "mark, unmark, delete, snooze, on, or bye.");
    }
}
