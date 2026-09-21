package auto.command;

/**
 * Contains the user-facing result produced by an executed command.
 *
 * @param message Message to display to the user.
 */
public record CommandResult(String message) {
}
