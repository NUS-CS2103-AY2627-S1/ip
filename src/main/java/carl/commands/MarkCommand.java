package carl.commands;

import carl.exceptions.CarlException;
import carl.task.Task;
import carl.task.TaskList;
import carl.task.TaskManager;
import carl.ui.Ui;

/**
 * Represents a command to mark a task as completed in the task list.
 */
public class MarkCommand extends TargetedTaskCommand {

    /**
     * Constructs a command that completes the task at the specified one-based index.
     *
     * @param index one-based task index.
     */
    public MarkCommand(int index) {
        super(index);
    }

    /**
     * {@inheritDoc}
     * Marks the specified task as done, updates the storage, and informs the user.
     *
     * @param ui      User interface for interacting with the user.
     * @param storage Task manager handling task data persistence.
     * @param tasks   List of current tasks.
     * @param raw     Raw input string entered by the user.
     * @return result describing the completed task.
     * @throws CarlException If the task index is missing, invalid, or the task is already marked as done.
     */
    @Override
    public CommandResult execute(Ui ui, TaskManager storage, TaskList tasks, String raw) throws CarlException {
        Task task = tasks.markTaskAsDone(index - 1);
        return CommandResult.createSuccess(ui.showMarkTaskAsDone(task));
    }
}
