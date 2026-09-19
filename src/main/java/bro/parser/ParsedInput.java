package bro.parser;

import bro.command.Command;

/**
 * Represents a parsed command along with its argument string.
 *
 * @param command   The parsed Command type.
 * @param arguments The raw arguments following the command word.
 */
public record ParsedInput(Command command, String arguments) {
}

