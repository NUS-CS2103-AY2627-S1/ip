package squirtlebot.exception;

/**
 * Represents exceptions encountered during storage operations such as reading/writing.
 */
public class StorageException extends SquirtleBotException {

    /**
     * Constructs a StorageException with a message to describe the error encountered.
     *
     * @param message description of error encountered.
     */
    public StorageException(String message) {
        super(message);
    }

    /**
     * Constructs a StorageException with a message describing the error, and its underlying cause.
     *
     * @param message description of error encountered.
     * @param cause underlying cause of error.
     */
    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
