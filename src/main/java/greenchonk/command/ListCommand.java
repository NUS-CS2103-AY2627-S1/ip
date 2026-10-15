package greenchonk.command;

import greenchonk.storage.Storage;
import greenchonk.task.TaskList;
import greenchonk.ui.Ui;

/**
 * Displays every task in the task list.
 */
public class ListCommand extends Command {
    /**
     * Creates a command that displays the complete task list.
     */
    public ListCommand() {
    }

    /**
     * Displays every task without changing or persisting the task list.
     *
     * @param tasks the task list to display.
     * @param ui the UI that displays the tasks.
     * @param storage the unused task storage.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTaskList(tasks);
    }
}
