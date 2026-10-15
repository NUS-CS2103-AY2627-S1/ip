package squirtlebot;

/**
 * Stores output from executed commands.
 *
 * @param shouldExit boolean value indicating if user has indicated to exit the program.
 * @param message text output from command execution to be displayed.
 */
public record CommandResult(boolean shouldExit, String message) {
}
