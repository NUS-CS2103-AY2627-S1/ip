package elora;

/**
 * A reply from Elora to a single line of user input, paired with whether
 * it was an error. The GUI uses {@link #isError()} to style error replies
 * differently from normal ones, so mistakes catch the user's eye instead
 * of blending into the conversation.
 */
public final class Response {
    private final String text;
    private final boolean error;

    /**
     * Creates a Response. Private since callers must go through
     * {@link #of(String)} or {@link #error(String)} to make the error
     * flag explicit at every call site.
     *
     * @param text The reply text.
     * @param error Whether this reply reports an error.
     */
    private Response(String text, boolean error) {
        this.text = text;
        this.error = error;
    }

    /**
     * Creates a normal, successful response.
     *
     * @param text The reply text to show the user.
     * @return The response, marked as not an error.
     */
    public static Response of(String text) {
        return new Response(text, false);
    }

    /**
     * Creates a response reporting that something went wrong.
     *
     * @param text The error message to show the user.
     * @return The response, marked as an error.
     */
    public static Response error(String text) {
        return new Response(text, true);
    }

    /**
     * Returns this response's reply text.
     *
     * @return The text to show the user.
     */
    public String getText() {
        return text;
    }

    /**
     * Returns whether this response reports an error.
     *
     * @return true if this response was created via {@link #error(String)}.
     */
    public boolean isError() {
        return error;
    }
}
