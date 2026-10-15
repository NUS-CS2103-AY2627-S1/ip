package beemo;

/**
 * Contains the user-facing response and exit status produced by a command.
 *
 * @param response Response to display to the user.
 * @param isExit Whether Beemo should exit after displaying the response.
 */
public record CommandResult(String response, boolean isExit) {
}
