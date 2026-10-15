package nutrisoy.command;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nutrisoy.exception.DukeException;
import nutrisoy.task.Task;
import nutrisoy.task.TaskList;
import nutrisoy.ui.Ui;

/**
 * Represents an executable user command.
 */
public abstract class Command {
    /**
     * Executes this command using the application's current collaborators.
     *
     * @param tasks list of tasks to operate on
     * @param ui user interface used to display results
     * @throws DukeException if the command cannot be executed
     */
    public abstract void execute(TaskList tasks, Ui ui) throws DukeException;

    /**
     * Converts a user-provided one-based task index into a zero-based list index.
     *
     * @param indexString one-based task index supplied by the user
     * @param tasks list whose bounds should be checked
     * @param missingIndexMessage message to use when no index is supplied
     * @return zero-based task index
     * @throws DukeException if the index is missing, invalid, or out of range
     */
    protected int parseTaskIndex(String indexString, TaskList tasks, String missingIndexMessage)
            throws DukeException {
        if (indexString.isEmpty()) {
            throw new DukeException(missingIndexMessage);
        }
        try {
            int index = Integer.parseInt(indexString) - 1;
            if (index < 0 || index >= tasks.size()) {
                throw new DukeException("Task number out of range. You currently have " + tasks.size()
                        + " tasks.");
            }
            return index;
        } catch (NumberFormatException e) {
            throw new DukeException("The task number must be a valid integer.");
        }
    }

    /**
     * Parses the two arguments shared by tag and untag commands.
     *
     * @param arguments command arguments containing an index and tag name
     * @param commandName command name used in the usage message
     * @return the index and tag name arguments
     * @throws DukeException if the arguments are missing or malformed
     */
    protected static String[] parseTagArguments(String arguments, String commandName) throws DukeException {
        assert arguments != null && commandName != null : "Tag command arguments must not be null";
        if (arguments.isBlank()) {
            throw new DukeException("Usage: " + commandName + " [index] [tag]");
        }
        String[] parts = arguments.trim().split("\\s+");
        if (parts.length != 2) {
            throw new DukeException("Usage: " + commandName + " [index] [tag]");
        }
        if (!Task.isValidTagName(parts[1])) {
            throw new DukeException("Tags must start with a letter or number and contain only letters, "
                    + "numbers, hyphens, or underscores.");
        }
        return parts;
    }

    /**
     * Finds one standalone command parameter and rejects missing or repeated occurrences.
     *
     * @param arguments full command arguments
     * @param parameter parameter token to find
     * @param usage expected command format
     * @return index at which the parameter starts
     * @throws DukeException if the parameter is missing or repeated
     */
    protected static int findSingleParameter(String arguments, String parameter, String usage)
            throws DukeException {
        Pattern parameterPattern = Pattern.compile("(?<!\\S)" + Pattern.quote(parameter) + "(?!\\S)");
        Matcher matcher = parameterPattern.matcher(arguments);
        if (!matcher.find()) {
            throw new DukeException("Missing '" + parameter + "' parameter. Use: " + usage);
        }
        int parameterIndex = matcher.start();
        if (matcher.find()) {
            throw new DukeException("The '" + parameter + "' parameter can only be specified once.");
        }
        return parameterIndex;
    }

    /**
     * Normalizes and validates a task description before it is stored.
     *
     * @param description raw task description
     * @param taskType type of task used in error messages
     * @return normalized task description
     * @throws DukeException if the description is empty or contains reserved characters
     */
    protected static String validateDescription(String description, String taskType) throws DukeException {
        String normalizedDescription = description.trim().replaceAll("\\s+", " ");
        if (normalizedDescription.isEmpty()) {
            throw new DukeException("The description of a " + taskType + " cannot be empty.");
        }
        if (normalizedDescription.contains("|")) {
            throw new DukeException("Task descriptions cannot contain the reserved '|' character.");
        }
        return normalizedDescription;
    }

    /**
     * Adds a task only when an equivalent task is not already present.
     *
     * @param task task to add
     * @param tasks current task list
     * @param ui user interface used to display the result
     * @throws DukeException if an equivalent task already exists
     */
    protected static void addUniqueTask(Task task, TaskList tasks, Ui ui) throws DukeException {
        if (tasks.containsSameTask(task)) {
            throw new DukeException("That task already exists in your list.");
        }
        tasks.add(task);
        ui.showTaskAdded(task, tasks.size());
    }

    /**
     * Indicates whether this command ends the application.
     *
     * @return {@code true} if this command requests application exit; {@code false} otherwise
     */
    public boolean isExit() {
        return false;
    }

    /**
     * Indicates whether execution may change the task list or a task's state.
     *
     * @return true for commands that require writable storage
     */
    public boolean changesTasks() {
        return true;
    }
}
