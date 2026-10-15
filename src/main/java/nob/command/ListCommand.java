package nob.command;

import nob.exception.NobException;
import nob.storage.Storage;
import nob.task.TaskList;
import nob.ui.Ui;

/**
 * Displays the current task list.
 */
public class ListCommand extends Command {
    /** Displays the current task list. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws NobException {
        ui.showTaskList(tasks);
    }
}
