package anniechat.logic;

/** Contains the response produced after processing one chatbot command. */
public class CommandResult {
    private final String message;
    private final boolean exitRequested;

    /**
     * Creates a command result.
     *
     * @param message response to display to the user.
     * @param exitRequested whether the application should stop accepting commands.
     */
    public CommandResult(String message, boolean exitRequested) {
        this.message = message;
        this.exitRequested = exitRequested;
    }

    /**
     * Returns the response to display.
     *
     * @return response message.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Returns whether the user requested to exit.
     *
     * @return true if the application should stop accepting commands.
     */
    public boolean isExitRequested() {
        return exitRequested;
    }
}
