package yqr.command;

import yqr.storage.Storage;
import yqr.task.TaskList;
import yqr.ui.Ui;

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
     * Displays the current task list.
     *
     * @param tasks task list to display.
     * @param ui user interface used to display the list.
     * @param storage storage component, which this command does not modify.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTaskList(tasks.getTasks());
    }
}
