package kelore.exception;

/** Represents a malformed record in Kelore's persistent task data. */
public class CorruptedDataException extends StorageException {
    /**
     * Creates an exception identifying the malformed line.
     *
     * @param lineNumber One-based line number of the malformed record.
     */
    public CorruptedDataException(int lineNumber) {
        super("The data file is corrupted at line " + lineNumber + ".");
    }
}
