package rem.exception;

/**
 * Indicates that a note command does not contain note text.
 */
public class EmptyNoteException extends RemException {
    private static final String MESSAGE = "What should I keep in this note?";

    /**
     * Creates an exception for a missing or blank note.
     */
    public EmptyNoteException() {
        super(MESSAGE);
    }
}
