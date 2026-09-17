package yachiyo.command;

import yachiyo.storage.Storage;
import yachiyo.task.TaskList;
import yachiyo.ui.Ui;

/**
 * Displays every task in the task list.
 */
public class ListCommand extends Command {
    /**
     * Displays the current tasks without changing or saving them.
     *
     * @param tasks task list to display.
     * @param ui user interface used to display the tasks.
     * @param storage unused because listing does not change stored data.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showTaskList(tasks.getTasks());
    }
}
