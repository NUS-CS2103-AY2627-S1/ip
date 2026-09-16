package zucc.command;

import zucc.ZuccException;
import zucc.storage.Storage;
import zucc.task.Task;
import zucc.task.TaskList;
import zucc.ui.Ui;

/**
 * Deletes the task identified by the command's raw task-number argument.
 */
public final class DeleteCommand extends Command {
    /** Creates a delete command awaiting values from Parser. */
    DeleteCommand() {
        super("delete");
    }

    /**
     * Validates the task number before removing and persisting the selected task.
     *
     * @param tasks task collection to update.
     * @param ui user interface through which confirmation is shown.
     * @param storage persistent storage to update.
     * @throws ZuccException if the task number is invalid or persistence fails.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws ZuccException {
        int taskIndex = parseTaskIndex();
        Task removedTask = tasks.delete(taskIndex);
        storage.saveTasks(tasks);
        ui.showMessage("Noted. I've removed this task:\n  "
                + removedTask
                + "\nNow you have " + tasks.getTaskCount() + " tasks in the list.");
    }
}
