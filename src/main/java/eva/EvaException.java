package eva;

/**
 * Represents an error caused by invalid input or application data.
 */
public class EvaException extends Exception {

    /**
     * Creates an exception with the specified explanation.
     *
     * @param message Explanation of the error.
     */
    public EvaException(String message) {
        super(message);
    }
}
