package alex;

/**
 * Represents the outcome of processing a user command.
 *
 * @param text response text to show the user.
 * @param isError whether the command resulted in an error.
 * @param shouldExit whether the application should exit after showing the response.
 */
public record CommandResult(String text, boolean isError, boolean shouldExit) {

    /**
     * Creates a command result that keeps the application open.
     *
     * @param text response text to show the user.
     * @param isError whether the command resulted in an error.
     */
    public CommandResult(String text, boolean isError) {
        this(text, isError, false);
    }

    /**
     * Creates a successful command result.
     *
     * @param text response text to show the user.
     * @return successful command result.
     */
    public static CommandResult success(String text) {
        return new CommandResult(text, false);
    }

    /**
     * Creates an unsuccessful command result.
     *
     * @param text error text to show the user.
     * @return unsuccessful command result.
     */
    public static CommandResult error(String text) {
        return new CommandResult(text, true);
    }

    /**
     * Creates a successful command result that requests application exit.
     *
     * @param text response text to show the user.
     * @return successful command result that requests application exit.
     */
    public static CommandResult exit(String text) {
        return new CommandResult(text, false, true);
    }
}
