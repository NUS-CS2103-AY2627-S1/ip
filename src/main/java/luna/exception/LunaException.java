package luna.exception;

/**
 * Represents an error that Luna can present to the user.
 */
public class LunaException extends Exception {

    /**
     * Creates an exception containing a user-facing explanation.
     *
     * @param message Explanation of the error.
     */
    public LunaException(String message) {
        super(message);
    }
}
