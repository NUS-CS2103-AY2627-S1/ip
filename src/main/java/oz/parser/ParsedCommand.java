package oz.parser;

/**
 * Contains the command word and arguments parsed from one user input.
 *
 * @param commandWord First non-whitespace token identifying the command.
 * @param arguments Trimmed text following the command word, or an empty string.
 */
public record ParsedCommand(String commandWord, String arguments) {
}
