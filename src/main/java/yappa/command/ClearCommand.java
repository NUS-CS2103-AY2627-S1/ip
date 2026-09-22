package yappa.command;

import yappa.exception.YappaException;
import yappa.task.TaskList;
import yappa.ui.Ui;

/** Clears all tasks from the task list. */
public class ClearCommand extends Command {
    /**
     * Clears the task list and formats the result.
     *
     * @param tasks Task list to clear.
     * @param ui User interface used to format the response.
     * @return Formatted clearing response.
     * @throws YappaException If clearing fails.
     */
    @Override
    public String execute(
            TaskList tasks, Ui ui)
            throws YappaException {

        tasks.clear();
        return ui.showTaskCleared();

    }

    /** Returns whether this command changes the task list. */
    @Override
    public boolean modifiesTasks() {
        return true;
    }
}
