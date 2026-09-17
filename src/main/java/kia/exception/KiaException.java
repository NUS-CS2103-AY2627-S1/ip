package kia.exception;

/**
 * Represents an error caused by invalid input to Kia.
 */
public class KiaException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * Creates an input error with the supplied explanation.
     *
     * @param message the explanation shown to the user
     */
    public KiaException(String message) {
        super(message + " >:[");
    }
}
