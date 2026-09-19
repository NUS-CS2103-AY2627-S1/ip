package yuno.exception;

/**
 * Represents a failure to initialize, read, or write the task data file.
 */
public class FileStorageException extends YunoException {
    /**
     * Creates a file storage exception with the specified error message.
     *
     * @param message Error message to display.
     */
    public FileStorageException(String message) {
        super(message);
    }

    /**
     * Creates a file storage exception with the specified error message and cause.
     *
     * @param message Error message to display.
     * @param cause Error that caused this exception.
     */
    public FileStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
