package erina;

/**
 * One line of user input, already broken into the command asked for and
 * whatever text followed it.
 *
 * <p>A record is used because this is plain data: two values that travel
 * together from {@link Parser} to {@link Erina} and are never modified.
 *
 * @param command  the command the user asked for
 * @param argument everything after the command word, trimmed, or the empty
 *                 string if there was nothing after it
 */
public record ParsedCommand(Command command, String argument) {
}
