package gary.ui;

/**
 * Contains a chatbot message and its presentation type.
 *
 * @param message Text to display to the user.
 * @param type Presentation type of the response.
 */
public record ChatResponse(String message, ResponseType type) {
    /**
     * Creates a response with non-null content and presentation type.
     */
    public ChatResponse {
        assert message != null : "Response message should not be null";
        assert type != null : "Response type should not be null";
    }

    /**
     * Returns a successful response.
     *
     * @param message Text to display.
     * @return Successful response containing the text.
     */
    public static ChatResponse normal(String message) {
        return new ChatResponse(message, ResponseType.NORMAL);
    }

    /**
     * Returns a command-guide response.
     *
     * @param message Command guide to display.
     * @return Help response containing the guide.
     */
    public static ChatResponse help(String message) {
        return new ChatResponse(message, ResponseType.HELP);
    }

    /**
     * Returns an error response.
     *
     * @param message Error text to display.
     * @return Error response containing the text.
     */
    public static ChatResponse error(String message) {
        return new ChatResponse(message, ResponseType.ERROR);
    }

    /**
     * Returns a farewell response.
     *
     * @param message Farewell text to display.
     * @return Farewell response containing the text.
     */
    public static ChatResponse farewell(String message) {
        return new ChatResponse(message, ResponseType.FAREWELL);
    }
}
