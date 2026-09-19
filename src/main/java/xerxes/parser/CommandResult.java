package xerxes.parser;

/**
 * Represents the result of processing one user command.
 */
public class CommandResult {
    /** Message produced while processing the command. */
    private final String message;
    private final boolean shouldExit;
    private final boolean isError;

    /**
     * Creates a command result instance to store the command output.
     *
     * @param message Message of the command result.
     * @param isExiting Whether the application should exit.
     * @param isError Error status of the result.
     */
    public CommandResult(String message, boolean isExiting, boolean isError) {
        this.message = message;
        this.shouldExit = isExiting;
        this.isError = isError;
    }

    /**
     * Returns the command message.
     *
     * @return Message to display.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Returns whether the application should exit.
     *
     * @return True if the application should exit.
     */
    public boolean shouldExit() {
        return shouldExit;
    }

    /**
     * Returns whether the command failed.
     *
     * @return True if the result represents an error.
     */
    public boolean isError() {
        return isError;
    }
}
