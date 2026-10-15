package am.storage;

/**
 * Indicates that saved task data does not follow the expected format.
 */
public class CorruptedDataException extends Exception {
    /**
     * Creates an exception with a description of the invalid data.
     *
     * @param message explanation of the corruption
     */
    public CorruptedDataException(String message) {
        super(message);
    }

    /**
     * Creates an exception with a description and the original parsing error.
     *
     * @param message explanation of the corruption
     * @param cause original parsing error
     */
    public CorruptedDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
