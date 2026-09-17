package nova.command;

import nova.exception.NovaException;
import nova.task.TaskList;

/**
 * Represents a command that targets a task by its user-facing task number.
 */
public abstract class TaskCommand extends Command {
    private final int taskNumber;
    private final String commandName;

    /**
     * Creates a task-targeting command.
     *
     * @param taskNumber one-based task number entered by the user.
     * @param commandName command keyword used in error guidance.
     */
    protected TaskCommand(int taskNumber, String commandName) {
        this.taskNumber = taskNumber;
        this.commandName = commandName;
    }

    /**
     * Validates the task number against the current list and converts it to an index.
     *
     * @param tasks current task list.
     * @return zero-based index of the selected task.
     * @throws NovaException if the list is empty or the task number does not exist.
     */
    protected final int resolveTaskIndex(TaskList tasks) throws NovaException {
        if (tasks.size() == 0) {
            throw new NovaException("There are no tasks to " + commandName + " yet. Add a task first.");
        }
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new NovaException("Task " + taskNumber + " does not exist. Choose a number from 1 to "
                    + tasks.size() + ".");
        }
        int taskIndex = taskNumber - 1;
        assert taskIndex >= 0 && taskIndex < tasks.size()
                : "Validated task number must map to an existing task index";
        return taskIndex;
    }
}
