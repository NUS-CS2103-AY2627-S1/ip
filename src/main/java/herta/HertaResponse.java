package herta;

/**
 * Represents the result of processing one command for the graphical user interface.
 */
public class HertaResponse {
    private final String message;
    private final boolean isExitRequested;
    private final ResponseCategory responseCategory;

    /**
     * Creates a response with its message, exit status, and semantic category.
     *
     * @param message the message to display.
     * @param isExitRequested whether the application should exit.
     * @param responseCategory the semantic category of the response.
     */
    public HertaResponse(String message, boolean isExitRequested, ResponseCategory responseCategory) {
        this.message = message;
        this.isExitRequested = isExitRequested;
        this.responseCategory = responseCategory;
    }

    /**
     * Returns the message generated while processing the command.
     *
     * @return the response message.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Indicates whether the command requested application exit.
     *
     * @return {@code true} if the application should exit.
     */
    public boolean isExitRequested() {
        return isExitRequested;
    }

    /**
     * Returns the semantic category of the response.
     *
     * @return the response category.
     */
    public ResponseCategory getResponseCategory() {
        return responseCategory;
    }
}
