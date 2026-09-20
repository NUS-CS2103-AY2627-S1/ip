package goop.command;

import goop.storage.Storage;
import goop.task.TaskList;
import goop.ui.Ui;

/**
 * Represents a request to display every task.
 */
public class ListCommand extends Command {
    /**
     * Creates a list command.
     */
    public ListCommand() {
    }

    /**
     * Displays the current task list.
     *
     * @param tasks Task list to display.
     * @param ui User interface used to display the task list.
     * @param storage Task storage, which is not accessed.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTaskList(tasks);
    }
}
