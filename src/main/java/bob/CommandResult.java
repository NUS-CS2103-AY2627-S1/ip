package bob;

/**
 * Contains the message and error status produced by executing a user command.
 *
 * @param message Response to display to the user.
 * @param isError Whether the command produced an error.
 */
public record CommandResult(String message, boolean isError) {
}
