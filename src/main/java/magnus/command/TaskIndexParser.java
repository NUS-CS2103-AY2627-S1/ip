package magnus.command;

import java.util.regex.Pattern;

import magnus.exception.CommandSyntaxException;
import magnus.exception.TaskNotFoundException;

/**
 * Parses and validates the one-based task number accepted by task-specific commands.
 */
final class TaskIndexParser {
    private static final Pattern UNSIGNED_INTEGER_PATTERN = Pattern.compile("[0-9]+");

    private TaskIndexParser() {
    }

    /**
     * Returns the zero-based index identified by a command's single task-number argument.
     *
     * @param arguments The command arguments to validate.
     * @param commandKeyword The command keyword used in syntax guidance.
     * @param taskCount The number of tasks that can be selected.
     * @return The validated zero-based task index.
     * @throws CommandSyntaxException If the task number is missing, non-numeric, or accompanied
     *                                by extra arguments.
     * @throws TaskNotFoundException If the task number does not identify an existing task.
     */
    static int parseTaskIndex(String[] arguments, String commandKeyword, int taskCount)
            throws CommandSyntaxException, TaskNotFoundException {
        if (arguments.length == 0 || arguments[0].isBlank()) {
            throw new CommandSyntaxException("\tThat move is incomplete - please tell me the task number.\n"
                    + createUsageMessage(commandKeyword));
        }

        if (arguments.length > 1) {
            throw new CommandSyntaxException("\tToo many pieces in that move - the " + commandKeyword
                    + " command accepts only one argument.\n" + createUsageMessage(commandKeyword));
        }

        if (!UNSIGNED_INTEGER_PATTERN.matcher(arguments[0]).matches()) {
            throw createInvalidTaskNumberException(arguments[0], commandKeyword);
        }

        int taskIndex;
        try {
            taskIndex = Integer.parseInt(arguments[0]) - 1;
        } catch (NumberFormatException exception) {
            throw createInvalidTaskNumberException(arguments[0], commandKeyword);
        }

        if (taskIndex < 0 || taskIndex >= taskCount) {
            throw new TaskNotFoundException("\tThat task is off the board - I can't find task number "
                    + arguments[0] + ".");
        }
        return taskIndex;
    }

    /**
     * Creates syntax guidance for a malformed or excessively large task number.
     *
     * @param taskNumber The invalid task-number text.
     * @param commandKeyword The command keyword to display.
     * @return The exception containing an actionable error message.
     */
    private static CommandSyntaxException createInvalidTaskNumberException(
            String taskNumber, String commandKeyword) {
        return new CommandSyntaxException("\tThat square is not a task number - '" + taskNumber
                + "' is not valid.\n" + createUsageMessage(commandKeyword));
    }

    /**
     * Creates task-number usage guidance for the specified command.
     *
     * @param commandKeyword The command keyword to display.
     * @return The formatted usage guidance.
     */
    private static String createUsageMessage(String commandKeyword) {
        return "\tUsage: " + commandKeyword + " <task number>";
    }
}
