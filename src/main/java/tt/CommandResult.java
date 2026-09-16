package tt;

/**
 * Contains the message and display status produced by a command.
 *
 * @param message message to show to the user.
 * @param isError whether the message describes an invalid command.
 */
record CommandResult(String message, boolean isError) {

    /**
     * Creates a result for a successfully processed command.
     */
    static CommandResult success(String message) {
        return new CommandResult(message, false);
    }

    /**
     * Creates a result for a command that could not be processed.
     */
    static CommandResult error(String message) {
        return new CommandResult(message, true);
    }
}
