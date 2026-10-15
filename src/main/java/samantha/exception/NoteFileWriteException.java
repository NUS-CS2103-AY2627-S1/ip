package samantha.exception;

/**
 * Indicates that the current note list could not be saved.
 */
public class NoteFileWriteException extends StorageException {
    /**
     * Creates an exception for a failed note-file write.
     *
     * @param cause underlying file-system failure
     */
    public NoteFileWriteException(Throwable cause) {
        super("I couldn't save your notes to disk.", cause);
    }
}
