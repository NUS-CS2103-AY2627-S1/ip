package yappa.command;

import yappa.exception.YappaException;
import yappa.task.Task;
import yappa.task.TaskList;
import yappa.ui.Ui;

/**
 * Represents a command that marks a task as done.
 */
public class MarkCommand extends Command {
    private final int taskIndex;

    /**
     * Creates a mark command for the specified task index.
     *
     * @param taskIndex Zero-based task index.
     */
    public MarkCommand(int taskIndex) {
        this.taskIndex = taskIndex;
    }

    /**
     * Marks the task and formats the result.
     *
     * @param tasks Task list to update.
     * @param ui User interface used to format the response.
     * @return Formatted marking response.
     * @throws YappaException If the task index is invalid.
     */
    @Override
    public String execute(TaskList tasks, Ui ui) throws YappaException {

        Task task = tasks.mark(taskIndex);

        return ui.showTaskMarked(task.getDescription());
    }

    /** Returns whether this command changes the task list. */
    @Override
    public boolean modifiesTasks() {
        return true;
    }
}
