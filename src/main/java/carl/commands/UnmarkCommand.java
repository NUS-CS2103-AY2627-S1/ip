package carl.commands;

import carl.exceptions.CarlException;
import carl.task.Task;
import carl.task.TaskList;
import carl.task.TaskManager;
import carl.ui.Ui;

/**
 * Represents a command to mark a task as not done in the task list.
 */
public class UnmarkCommand extends TargetedTaskCommand {

    /**
     * Constructs a command that reopens the task at the specified one-based index.
     *
     * @param index one-based task index.
     */
    public UnmarkCommand(int index) {
        super(index);
    }

    /**
     * {@inheritDoc}
     * Unmarks the specified task as not done, updates the storage, and informs the user.
     *
     * @param ui      User interface for interacting with the user.
     * @param storage Task manager handling task data persistence.
     * @param tasks   List of current tasks.
     * @param raw     Raw input string entered by the user.
     * @return result describing the reopened task.
     * @throws CarlException If the task index is missing, invalid, or the task is already not done.
     */
    @Override
    public CommandResult execute(Ui ui, TaskManager storage, TaskList tasks, String raw) throws CarlException {
        Task task = tasks.markTaskAsUndone(index - 1);

        return CommandResult.createSuccess(ui.showUnmarkTask(task));
    }
}
