package yappa.command;

import yappa.exception.YappaException;
import yappa.task.Task;
import yappa.task.TaskList;
import yappa.ui.Ui;

/** Represents a command that marks a task as incomplete. */
public class UnmarkCommand extends Command {
    private final int taskIndex;

    /**
     * Creates an unmark command for the specified task index.
     *
     * @param taskIndex Zero-based task index.
     */
    public UnmarkCommand(int taskIndex) {
        this.taskIndex = taskIndex;
    }

    /**
     * Marks the task as incomplete and formats the result.
     *
     * @param tasks Task list to update.
     * @param ui User interface used to format the response.
     * @return Formatted unmarking response.
     * @throws YappaException If the task index is invalid.
     */
    @Override
    public String execute(TaskList tasks, Ui ui) throws YappaException {

        Task task = tasks.unmark(taskIndex);

        return ui.showTaskUnmarked(task.getDescription());
    }

    /** Returns whether this command changes the task list. */
    @Override
    public boolean modifiesTasks() {
        return true;
    }
}
