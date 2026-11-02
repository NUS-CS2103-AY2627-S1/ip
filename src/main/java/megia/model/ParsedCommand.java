package megia.model;

/**
 * Represents user input separated into its command name and command body.
 *
 * @param commandName Name identifying the requested command.
 * @param body Input following the command name.
 */
public record ParsedCommand(
        String commandName,
        String body) {
}
