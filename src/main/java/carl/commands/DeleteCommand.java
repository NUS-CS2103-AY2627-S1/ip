package carl.commands;

import carl.exceptions.CarlException;
import carl.task.Task;
import carl.task.TaskList;
import carl.task.TaskManager;
import carl.ui.Ui;

/**
 * Represents a command to delete an existing task from the task list.
 */
public class DeleteCommand extends TargetedTaskCommand {

    /**
     * Constructs a command that deletes the task at the specified one-based index.
     *
     * @param index one-based task index.
     */
    public DeleteCommand(int index) {
        super(index);
    }

    /**
     * {@inheritDoc}
     * Deletes the specified task by its 1-based index, saves the updated list, and notifies the user.
     *
     * @param ui      User interface for interacting with the user.
     * @param storage Task manager handling task data persistence.
     * @param tasks   List of current tasks.
     * @param raw     Raw input string entered by the user.
     * @return result describing the deleted task and remaining task count.
     * @throws CarlException If the index argument is missing, invalid, or out of range.
     */
    @Override
    public CommandResult execute(Ui ui, TaskManager storage, TaskList tasks, String raw) throws CarlException {
        Task task = tasks.deleteTask(index - 1);
        return CommandResult.createSuccess(ui.showDeleteTask(task, tasks.getTasksLeft()));
    }
}
