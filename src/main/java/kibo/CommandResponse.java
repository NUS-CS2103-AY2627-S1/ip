package kibo;

/**
 * Carries a command's message and outcome so interfaces can distinguish errors from normal replies.
 */
public final class CommandResponse {
    private final String message;
    private final boolean isError;

    /**
     * Creates a response with its message and error status.
     *
     * @param message text to display.
     * @param isError whether command execution failed.
     */
    public CommandResponse(String message, boolean isError) {
        this.message = message;
        this.isError = isError;
    }

    /**
     * Returns the message to display.
     *
     * @return response text.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Returns whether command execution failed.
     *
     * @return true for an error response.
     */
    public boolean isError() {
        return isError;
    }
}
