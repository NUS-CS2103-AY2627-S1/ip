package nob.command;

import nob.storage.Storage;
import nob.task.TaskList;
import nob.ui.Ui;

/**
 * Removes every task from the task list.
 */
public class ClearCommand extends Command {
    /** Clears tasks, saves the empty list, and reports the result. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        tasks.clear();
        if (!storage.saveTasks(tasks.getTasks(), tasks.getTaskCount())) {
            ui.showSavingError();
        }
        ui.showTasksCleared();
    }
}
