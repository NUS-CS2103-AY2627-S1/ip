package tasque;

/**
 * Carries a formatted Tasque response and its error status.
 */
public final class TasqueResponse {
    private final String text;
    private final boolean isError;

    /**
     * Creates an immutable response with text that is ready to display.
     *
     * @param text Already-formatted response text.
     * @param isError Whether the response represents an error.
     */
    public TasqueResponse(String text, boolean isError) {
        this.text = text;
        this.isError = isError;
    }

    public String getText() {
        return this.text;
    }

    public boolean isError() {
        return this.isError;
    }
}
