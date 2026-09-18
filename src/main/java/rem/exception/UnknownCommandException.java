package rem.exception;

/**
 * Indicates that the user entered a command that Rem does not recognize.
 */
public class UnknownCommandException extends RemException {
    private static final String MESSAGE = "Hmm... I don't know what to do with that..."
            + "\nTry todo read book to add a task, or list to view tasks."
            + "\nCommands: todo, deadline, event, list, find, on, mark, unmark, delete, note, deletenote, bye.";

    /**
     * Creates an exception for an unknown or blank command.
     */
    public UnknownCommandException() {
        super(MESSAGE);
    }
}
