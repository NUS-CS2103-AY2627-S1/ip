package alice;

/**
 * A single reply from Alice: the text to show the user, together with
 * whether that text describes something that went wrong.
 *
 * <p>Carrying both in one object lets a front end react to failures
 * without having to inspect the wording of the message. The JavaFX GUI
 * (see {@link MainWindow}) uses {@link #isError()} to style failures
 * differently from ordinary replies, which it could not do while
 * {@link Alice#getResponse} returned a bare String.
 */
public class Response {
    private final String text;
    private final boolean isError;

    private Response(String text, boolean isError) {
        assert text != null : "response text must not be null";
        this.text = text;
        this.isError = isError;
    }

    /**
     * Creates a reply for a command that completed successfully.
     *
     * @param text the message to show the user.
     * @return a response marked as not being an error.
     */
    public static Response ofSuccess(String text) {
        return new Response(text, false);
    }

    /**
     * Creates a reply for a command that failed.
     *
     * @param text the error message to show the user.
     * @return a response marked as an error.
     */
    public static Response ofError(String text) {
        return new Response(text, true);
    }

    /**
     * Returns the text of this reply.
     *
     * @return the message to show the user.
     */
    public String getText() {
        return text;
    }

    /**
     * Returns whether this reply reports a failure.
     *
     * @return true if the command did not succeed.
     */
    public boolean isError() {
        return isError;
    }

    /**
     * Returns the reply text, so a response prints as its message.
     *
     * @return the message to show the user.
     */
    @Override
    public String toString() {
        return text;
    }
}
