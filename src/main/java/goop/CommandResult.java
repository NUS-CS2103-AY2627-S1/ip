package goop;

/**
 * Stores the response and exit status produced by a command.
 */
public final class CommandResult {
    private final String response;
    private final boolean isExit;

    /**
     * Creates a result for an executed command.
     *
     * @param response Text to display to the user.
     * @param isExit Whether the application should exit after displaying the response.
     */
    public CommandResult(String response, boolean isExit) {
        this.response = response;
        this.isExit = isExit;
    }

    public String getResponse() {
        return response;
    }

    public boolean isExit() {
        return isExit;
    }
}
