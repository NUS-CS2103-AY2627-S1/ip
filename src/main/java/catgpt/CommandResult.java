package catgpt;

/**
 * Contains the visible result of a command and whether it ends the application.
 *
 * @param response Message to show the user.
 * @param shouldExit Whether the application should exit after showing the message.
 */
public record CommandResult(String response, boolean shouldExit) {
}
