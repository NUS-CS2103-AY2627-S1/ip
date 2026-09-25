package shan.exception;

/**
 * Represents a failure to read or write Shan's task data file.
 */
public class DataFileException extends ShanException {
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a data-file exception.
     *
     * @param message Explanation suitable for displaying to the user.
     */
    public DataFileException(String message) {
        super(message);
    }
}
