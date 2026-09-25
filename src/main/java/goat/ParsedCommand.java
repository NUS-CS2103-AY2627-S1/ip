package goat;

/**
 * Contains a recognized command type and its remaining argument text.
 *
 * @param type recognized command type
 * @param commandWord first word entered by the user
 * @param argument text following the command word
 */
public record ParsedCommand(CommandType type, String commandWord, String argument) {
}
