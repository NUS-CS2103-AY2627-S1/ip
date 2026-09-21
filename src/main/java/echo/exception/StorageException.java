package echo.exception;

/**
 * An {@link EchoException} for storage related exceptions.
 */
public class StorageException extends EchoException {
    /**
     * Creates a {@link StorageException} describing the storage problem.
     * @param message explanation of what went wrong.
     */
    public StorageException(String message) {
        super("Mission archive problem: " + message);
    }
}
