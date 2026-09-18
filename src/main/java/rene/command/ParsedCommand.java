package rene.command;

/**
 * Represents a recognized command and the text supplied after its keyword.
 *
 * @param type the recognized command type.
 * @param argument the text following the command keyword.
 */
public record ParsedCommand(CommandType type, String argument) {
}
