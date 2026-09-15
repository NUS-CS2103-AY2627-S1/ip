package duke;

/**
 * Represents the text and display type of a response to a user command.
 *
 * @param text Text to display to the user.
 * @param isError Whether the response describes an error.
 * @param shouldExit Whether the application should exit after displaying the response.
 */
public record CommandResponse(String text, boolean isError, boolean shouldExit) {
    /**
     * Creates a response that does not request application exit.
     *
     * @param text Text to display to the user.
     * @param isError Whether the response describes an error.
     */
    public CommandResponse(String text, boolean isError) {
        this(text, isError, false);
    }
}
