package jiji.exception;

/**
 * Exception thrown when reading from or writing to the data storage file fails.
 */
public class JijiStorageException extends JijiException {

    /**
     * Constructs a JijiStorageException with the specified error message.
     *
     * @param message The detailed storage error message.
     */
    public JijiStorageException(String message) {
        super(message);
    }
}
