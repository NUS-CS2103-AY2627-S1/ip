package echo.exception;

/**
 * An {@link StorageException} for when the saved stored data does not match the accepted format.
 */
public class CorruptFormatException extends StorageException {
    /**
     * Creates a {@link CorruptFormatException}.
     * @param saveFormat the offending improperly formatted saved format.
     */
    public CorruptFormatException(String saveFormat) {
        super("I could not understand this record: " + saveFormat);
    }

    /**
     * Creates an error identifying an invalid save-file line.
     *
     * @param lineNumber One-indexed line number.
     * @param saveFormat Invalid record text.
     * @param reason Reason the record is invalid.
     */
    public CorruptFormatException(int lineNumber, String saveFormat, String reason) {
        super("I could not understand line " + lineNumber + " because " + reason + ": " + saveFormat);
    }
}
