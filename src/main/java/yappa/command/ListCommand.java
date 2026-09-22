package yappa.command;

import yappa.exception.YappaException;
import yappa.task.TaskList;
import yappa.ui.Ui;

/** Lists all tasks in their current order. */
public class ListCommand extends Command {
    /**
     * Formats the current task list.
     *
     * @param tasks Task list to display.
     * @param ui    User interface used to format the response.
     * @return Formatted task-list response.
     * @throws YappaException If the list cannot be displayed.
     */
    @Override
    public String execute(TaskList tasks, Ui ui) throws YappaException {
        return ui.showTaskList(tasks);
    }

    /** Returns whether this command modifies the task list. */
    @Override
    public boolean modifiesTasks() {
        return false;
    }

}
