package potatobot.logic;

/**
 * Represents the user-visible outcome of executing one command.
 *
 * @param message Response to show to the user.
 * @param isExit  Whether the application should stop accepting commands.
 * @param isError Whether the command failed and its response should be
 *                highlighted.
 */
public record CommandResult(String message, boolean isExit, boolean isError) {
    /**
     * Creates a non-exit result with the specified message.
     *
     * @param message Response to show to the user.
     * @return Result that keeps the application running.
     */
    public static CommandResult reply(String message) {
        return new CommandResult(message, false, false);
    }

    /**
     * Creates an error result that keeps the application running.
     *
     * @param message Explanation of the failure to show to the user.
     * @return Result identifying a failed command.
     */
    public static CommandResult error(String message) {
        return new CommandResult(message, false, true);
    }

    /**
     * Creates an exit result with the specified message.
     *
     * @param message Final response to show to the user.
     * @return Result that tells the application to stop.
     */
    public static CommandResult exit(String message) {
        return new CommandResult(message, true, false);
    }
}
