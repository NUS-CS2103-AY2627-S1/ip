package sal;

/**
 * A chatbot reply to display in the GUI.
 */
public class ChatResponse {
    private final String message;
    private final boolean isError;

    /**
     * Creates a reply.
     *
     * @param message Text to show in the dialog bubble.
     * @param isError {@code true} if this reply is an error message.
     */
    public ChatResponse(String message, boolean isError) {
        this.message = message;
        this.isError = isError;
    }

    public String getMessage() {
        return message;
    }

    /**
     * Returns whether this reply should be shown as an error.
     *
     * @return {@code true} if the command failed.
     */
    public boolean isError() {
        return isError;
    }
}
