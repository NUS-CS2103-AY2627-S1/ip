package sioet.command;

import sioet.storage.Storage;
import sioet.task.TaskList;
import sioet.ui.Ui;

/**
 * Represents the command that displays all tasks.
 */
public class ListCommand extends Command {

    /**
     * Executes the list command.
     *
     * @param tasks   the task list
     * @param ui      the user interface
     * @param storage the task storage
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTasks(tasks);
    }
}
