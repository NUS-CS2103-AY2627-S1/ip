package littledaisy.command;

/**
 * A command word paired with the remaining text on its input line.
 *
 * @param command recognized command word
 * @param arguments text following the command word
 */
public record ParsedCommand(Command command, String arguments) {
}
