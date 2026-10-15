package tangent.command;

import tangent.storage.Storage;
import tangent.task.TaskList;
import tangent.ui.Ui;

/** Lists all tasks currently in the task list. */
public class ListCommand extends Command {

    /**
     * Displays all tasks currently present in {@code tasks}.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        tasks.resetDisplayedIndexes();
        ui.showCurrentTasks(tasks);
    }
}
