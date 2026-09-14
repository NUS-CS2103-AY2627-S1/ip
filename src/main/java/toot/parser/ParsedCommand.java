package toot.parser;

/**
 * Holds the recognized type and arguments of one user command.
 *
 * @param type Recognized command type.
 * @param arguments Trimmed text following the command keyword.
 */
public record ParsedCommand(CommandType type, String arguments) {
}
