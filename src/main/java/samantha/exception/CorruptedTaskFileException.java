package samantha.exception;

/**
 * Indicates that a saved task file contains an invalid record.
 */
public class CorruptedTaskFileException extends StorageException {
    private final int lineNumber;

    /**
     * Creates an exception for a malformed record at the given line.
     *
     * @param lineNumber one-based line number containing the malformed record
     * @param cause parsing failure that identified the malformed record
     */
    public CorruptedTaskFileException(int lineNumber, Throwable cause) {
        super("The data file is corrupted at line " + lineNumber + ".", cause);
        this.lineNumber = lineNumber;
    }

    /**
     * Returns the one-based line number containing the malformed record.
     *
     * @return corrupted record line number
     */
    public int getLineNumber() {
        return lineNumber;
    }
}
