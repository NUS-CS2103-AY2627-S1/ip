package rem.exception;

/**
 * Indicates that a task has no note to delete.
 */
public class MissingNoteException extends RemException {
    private static final String MESSAGE = "This task doesn't have a note to remove.";

    /**
     * Creates an exception for deleting a nonexistent note.
     */
    public MissingNoteException() {
        super(MESSAGE);
    }
}
