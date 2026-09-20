package larper;

/**
 * Stores Larper's reply to one user input.
 * The response also records whether the current session should end after the reply is shown.
 */
public class LarperResponse {
    private final String message;
    private final boolean isExit;
    private final boolean isError;

    /**
     * Creates a non-error response with its message and exit status.
     *
     * @param message Text Larper should show to the user.
     * @param isExit Whether this response should end the current session.
     */
    public LarperResponse(String message, boolean isExit) {
        this(message, isExit, false);
    }

    /**
     * Creates a response with its message, exit status, and error status.
     *
     * @param message Text Larper should show to the user.
     * @param isExit Whether this response should end the current session.
     * @param isError Whether this response should be highlighted as an error.
     */
    public LarperResponse(String message, boolean isExit, boolean isError) {
        assert message != null : "Larper response message should never be null.";
        this.message = message;
        this.isExit = isExit;
        this.isError = isError;
    }

    public String getMessage() {
        return message;
    }

    public boolean isExit() {
        return isExit;
    }

    public boolean isError() {
        return isError;
    }
}
