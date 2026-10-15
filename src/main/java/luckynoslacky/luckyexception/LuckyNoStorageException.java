package luckynoslacky.luckyexception;

/**
 * Indicates that the chatbot could not read or write its task data.
 */
public class LuckyNoStorageException extends RuntimeException {
    /**
     * Creates a storage error with a message.
     *
     * @param message explanation of the storage error
     */
    public LuckyNoStorageException(String message) {
        super(message);
    }

    /**
     * Creates a storage error with a message and underlying cause.
     *
     * @param message explanation of the storage error
     * @param cause underlying I/O or parsing failure
     */
    public LuckyNoStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
