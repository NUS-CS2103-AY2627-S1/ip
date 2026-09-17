package panda.exception.storage;

/**
 * Indicates that a saved task record does not follow Panda's storage format.
 */
public class FileCorruptedException extends Exception {

    /**
     * Creates an exception describing the invalid record.
     *
     * @param message Description of the format problem.
     */
    public FileCorruptedException(String message) {
        super(message);
    }

    /**
     * Creates an exception describing the invalid record and its cause.
     *
     * @param message Description of the format problem.
     * @param cause Error that revealed the corrupted record.
     */
    public FileCorruptedException(String message, Throwable cause) {
        super(message, cause);
    }
}
