package jason.exception;

/**
 * Indicates that a requested task index is not present.
 */
public class InvalidIndexException extends Exception {
    /**
     * Creates an invalid-index exception.
     */
    public InvalidIndexException() {
        super();
    }

    /**
     * Returns the invalid-index error message.
     *
     * @return invalid-index error message.
     */
    @Override
    public String toString() {
        return "No such index in list.";
    }
}
