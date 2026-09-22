package oz;

/**
 * Contains the user-facing result of executing a command.
 *
 * @param message User-facing response message.
 * @param type Category used to style or interpret the response.
 */
public record CommandResult(String message, ResponseType type) {
}
