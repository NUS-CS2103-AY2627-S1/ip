package slotbot;

/**
 * Contains the text and error status produced for one command.
 *
 * @param text Formatted response text.
 * @param isError Whether the command produced a user-facing error.
 */
public record BotResponse(String text, boolean isError) {
    /**
     * Creates an immutable command response.
     */
    public BotResponse {
        assert text != null : "Response text must not be null";
    }
}
