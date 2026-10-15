package serina.parser;

import java.util.List;

/**
 * Stores a validated command type and its normalized arguments.
 *
 * @param type Command to execute.
 * @param arguments Command arguments in semantic order.
 */
public record ParsedCommand(CommandType type, List<String> arguments) {
    /**
     * Creates an immutable parsed command.
     */
    public ParsedCommand {
        arguments = List.copyOf(arguments);
    }
}
