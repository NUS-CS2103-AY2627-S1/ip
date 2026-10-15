package samantha.exception;

/**
 * Indicates that the saved note file contains an invalid record.
 */
public class CorruptedNoteFileException extends StorageException {
    private final int lineNumber;

    /**
     * Creates an exception for a malformed note at the given line.
     *
     * @param lineNumber one-based line number containing the malformed note
     * @param cause parsing failure that identified the malformed note
     */
    public CorruptedNoteFileException(int lineNumber, Throwable cause) {
        super("The note file is corrupted at line " + lineNumber + ".", cause);
        this.lineNumber = lineNumber;
    }

    /**
     * Returns the one-based line number containing the malformed note.
     *
     * @return corrupted record line number
     */
    public int getLineNumber() {
        return lineNumber;
    }
}
