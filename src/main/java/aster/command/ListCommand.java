package aster.command;

import aster.storage.Storage;
import aster.task.TaskList;
import aster.ui.Ui;

/**
 * Shows the tasks currently in the list.
 *
 * <p>Showing the list leaves it as it was, so this command never saves.
 */
public class ListCommand extends Command {
    /**
     * Creates a command that shows the whole task list.
     */
    public ListCommand() {
    }

    /**
     * Shows the tasks in the order they were added.
     *
     * @param tasks the task list to show.
     * @param ui the user interface to show it through.
     * @param storage the store; unused, because showing the list changes nothing.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTasks(tasks.asList());
    }
}
