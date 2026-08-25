package matthew.exception;

/** Represents an expected error while processing a Matthew operation. */
public class MatthewException extends Exception {

    /**
     * Creates an exception with the specified explanation.
     *
     * @param message Explanation of the error.
     */
    public MatthewException(String message) {
        super(message);
    }
}
