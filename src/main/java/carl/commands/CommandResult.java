package carl.commands;

/**
 * Represents the result of a command execution.
 * Encapsulates the response message to display to the user and execution status flags.
 *
 * @param message  The feedback text to be shown to the user.
 * @param isExited Whether the command execution should cause the application to exit.
 * @param isError  Whether an error occurred during command execution.
 */
public record CommandResult(

        String message,
        boolean isExited,
        boolean isError
) {

    /**
     * Creates a successful {@code CommandResult} with the specified feedback message.
     *
     * @param message The feedback message to display to the user.
     * @return A {@code CommandResult} indicating successful execution.
     */
    public static CommandResult createSuccess(String message) {
        return new CommandResult(message, false, false);
    }

    /**
     * Creates a {@code CommandResult} indicating the application should terminate.
     *
     * @param message The exit message to display to the user.
     * @return A {@code CommandResult} with the exit flag set to true.
     */
    public static CommandResult createExit(String message) {
        return new CommandResult(message, true, false);
    }

    /**
     * Creates an error {@code CommandResult} with the specified error message.
     *
     * @param message The error message to display to the user.
     * @return A {@code CommandResult} with the error flag set to true.
     */
    public static CommandResult createError(String message) {
        return new CommandResult(message, false, true);
    }

}
