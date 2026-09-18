package meka.command;

import meka.storage.Storage;
import meka.task.TaskList;
import meka.ui.Ui;

/**
 * Displays all tasks in the task list.
 */
public class ListCommand extends Command {
    /**
     * Creates a command that displays the task list.
     */
    public ListCommand() {
    }

    /**
     * Shows the current task list without modifying it.
     *
     * @param tasks task list to display.
     * @param ui user interface through which tasks are displayed.
     * @param storage current storage component; not used.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTaskList(tasks);
    }
}
