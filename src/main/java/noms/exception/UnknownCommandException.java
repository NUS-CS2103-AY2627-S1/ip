package noms.exception;

/**
 * Indicates that Noms does not recognize a user's command.
 */
public class UnknownCommandException extends NomsException {
    /** Creates the exception with a message listing the valid commands. */
    public UnknownCommandException() {
        super("Grrr... Noms couldn't understand that command.\n"
                + "Try feeding me a todo, deadline, event, list, mark, unmark, delete, snooze, "
                + "on, or bye.");
    }
}
