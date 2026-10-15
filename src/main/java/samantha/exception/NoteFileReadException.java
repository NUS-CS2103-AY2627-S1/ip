package samantha.exception;

/**
 * Indicates that the saved note file could not be read.
 */
public class NoteFileReadException extends StorageException {
    /**
     * Creates an exception for a failed note-file read.
     *
     * @param cause underlying file-system failure
     */
    public NoteFileReadException(Throwable cause) {
        super("I couldn't read your saved notes.", cause);
    }
}
