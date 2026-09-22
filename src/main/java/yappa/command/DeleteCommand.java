package yappa.command;

import yappa.exception.YappaException;
import yappa.task.Task;
import yappa.task.TaskList;
import yappa.ui.Ui;

/** Deletes a task from the task list. */
public class DeleteCommand extends Command {
    private final int taskIndex;

    /**
     * Creates a delete command for the specified task index.
     *
     * @param taskIndex Zero-based task index.
     */
    public DeleteCommand(int taskIndex) {
        this.taskIndex = taskIndex;
    }

    /**
     * Deletes the task and formats the result.
     *
     * @param tasks Task list to update.
     * @param ui User interface used to format the response.
     * @return Formatted deletion response.
     * @throws YappaException If the task index is invalid.
     */
    @Override
    public String execute(TaskList tasks, Ui ui) throws YappaException {

        Task task = tasks.remove(taskIndex);
        return ui.showTaskDeleted(task, tasks.size());
    }

    /** Returns whether this command changes the task list. */
    @Override
    public boolean modifiesTasks() {
        return true;
    }
}
