package luckynoslacky;

/**
 * Represents the complete result of executing one chatbot command.
 *
 * @param content structured user-facing reply content
 * @param shouldExit whether the interface should close after the reply
 * @param tone semantic tone for presenting the reply
 */
public record CommandResult(
        ResponseContent content,
        boolean shouldExit,
        ResponseTone tone) {
    /** Validates the required result values. */
    public CommandResult {
        if (content == null) {
            throw new IllegalArgumentException("Result content cannot be null.");
        }
        if (tone == null) {
            throw new IllegalArgumentException("Result tone cannot be null.");
        }
    }

    /**
     * Creates a result with ordinary plain-text content.
     *
     * @param message user-facing reply text
     * @param shouldExit whether the interface should close after the reply
     * @param tone semantic tone for presenting the reply
     */
    public CommandResult(
            String message,
            boolean shouldExit,
            ResponseTone tone) {
        this(new TextContent(message), shouldExit, tone);
    }

    /**
     * Creates a neutral plain-text result.
     *
     * @param message user-facing reply text
     * @param shouldExit whether the interface should close after the reply
     */
    public CommandResult(String message, boolean shouldExit) {
        this(message, shouldExit, ResponseTone.NEUTRAL);
    }

    /**
     * Returns the user-facing response text.
     *
     * @return text representation of the response content
     */
    public String message() {
        return content.message();
    }
}
