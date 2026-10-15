package dude.command.core;

/**
 * Describes the application-level result of executing one command.
 *
 * @param shouldExit Whether the application should terminate.
 */
public record CommandResult(boolean shouldExit) {
    /**
     * Returns a result that keeps the application running.
     *
     * @return Non-exiting command result.
     */
    public static CommandResult continueRunning() {
        return new CommandResult(false);
    }

    /**
     * Returns a result that requests application termination.
     *
     * @return Exiting command result.
     */
    public static CommandResult exiting() {
        return new CommandResult(true);
    }
}
