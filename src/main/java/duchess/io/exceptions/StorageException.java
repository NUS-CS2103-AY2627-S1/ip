package duchess.io.exceptions;

/**
 * Represents the exceptions that may be encountered
 * when loading or saving tasks.
 */
public class StorageException extends Exception {

    /**
     * Creates a {@code StorageException} with the desired message.
     */
    public StorageException(String message) {
        super(message);
    }
}
