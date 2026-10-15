package magnus.command;

/**
 * Contains the type and user-facing message produced by an executed command.
 *
 * @param commandType The type of command that was executed.
 * @param message The message describing the result of the command.
 * @param hasTaskListChanged Whether processing the input changed the task list.
 */
public record CommandResult(CommandType commandType, String message, boolean hasTaskListChanged) {
}
