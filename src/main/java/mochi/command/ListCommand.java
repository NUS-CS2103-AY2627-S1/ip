package mochi.command;

import mochi.TaskList;
import mochi.ui.Ui;

/**
 * Lists all tasks in the task list.
 */
public class ListCommand extends Command {
    /** {@inheritDoc} */
    @Override
    public void execute(TaskList tasks, Ui ui) {
        ui.showTaskList(tasks.getTasks());
    }
}
