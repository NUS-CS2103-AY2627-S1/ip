package walle;

/**
 * WALLE's reply to one line of user input.
 *
 * @param text    the message to show the user.
 * @param isError whether the reply represents an error (e.g. a bad command
 *                or a corrupted save file), so a caller such as the GUI can
 *                style it differently from a normal reply.
 * @param isExit  whether this reply is WALLE's farewell to a {@code bye}
 *                command, so a caller such as the GUI knows to shut down.
 */
public record Response(String text, boolean isError, boolean isExit) {
}
