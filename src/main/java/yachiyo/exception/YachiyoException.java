package yachiyo.exception;

/**
 * Represents an error that prevents Yachiyo from completing an operation.
 */
public class YachiyoException extends Exception {
    /** Category used by graphical interfaces to distinguish error severity. */
    private final ErrorCategory category;

    /**
     * Creates an exception with the specified category and explanation.
     *
     * @param category category used to present the error.
     * @param message Explanation of the error.
     */
    public YachiyoException(ErrorCategory category, String message) {
        super(message);
        assert category != null : "Error category must not be null";

        this.category = category;
    }

    /**
     * Returns the category used to present this error.
     *
     * @return error presentation category.
     */
    public ErrorCategory getCategory() {
        return category;
    }
}
