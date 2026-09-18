package martin.exception;

/**
 * Represents a failure while creating, reading, or writing Martin's task
 * storage.
 */
public class StorageException extends MartinException {

    /**
     * Constructs a {@code StorageException} with a detail message and underlying
     * cause.
     *
     * @param message The detail error message.
     * @param cause   The underlying storage failure.
     */
    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
