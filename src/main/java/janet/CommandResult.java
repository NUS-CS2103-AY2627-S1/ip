package janet;

/**
 * Describes one processed command for presentation by a user interface.
 *
 * @param message response text to display.
 * @param isError whether the response reports an invalid command or failed operation.
 * @param isExit whether the command asks Janet to exit.
 */
public record CommandResult(String message, boolean isError, boolean isExit) {
}
