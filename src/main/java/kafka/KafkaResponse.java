package kafka;

/**
 * Represents the result of processing one user command.
 *
 * @param message Formatted response to display.
 * @param isError Whether the response represents an error.
 * @param action Follow-up action for the user interface.
 */
public record KafkaResponse(String message, boolean isError, Action action) {
    /**
     * Identifies actions that the user interface must handle itself.
     */
    public enum Action {
        NONE,
        CONFIRM_STORAGE_OVERWRITE,
        EXIT
    }

    /**
     * Creates a response that only needs to be displayed.
     */
    public KafkaResponse(String message, boolean isError) {
        this(message, isError, Action.NONE);
    }
}
