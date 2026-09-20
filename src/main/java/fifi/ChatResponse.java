package fifi;

/**
 * Carries a command's message and error status to the graphical interface.
 */
public final class ChatResponse {
    private final String message;
    private final boolean isError;

    /**
     * Creates an immutable response with its own error status.
     *
     * @param message text to display
     * @param isError whether the command failed
     */
    public ChatResponse(String message, boolean isError) {
        this.message = message;
        this.isError = isError;
    }

    public String getMessage() {
        return message;
    }

    public boolean isError() {
        return isError;
    }
}
