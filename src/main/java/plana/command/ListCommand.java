package plana.command;

import plana.storage.Storage;
import plana.task.TaskList;
import plana.ui.Ui;

/**
 * Displays all tasks in their current order.
 */
public class ListCommand extends Command {
    /**
     * Displays the current task list.
     *
     * @param tasks the task list to display.
     * @param ui the user interface used for the list response.
     * @param storage unused by this command.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTaskList(tasks);
    }
}
