package zucc.command;

import zucc.ZuccException;
import zucc.storage.Storage;
import zucc.task.Task;
import zucc.task.TaskList;
import zucc.ui.Ui;

/**
 * Marks the task identified by the command's raw task-number argument as done.
 */
public final class MarkCommand extends Command {
    /** Creates a mark command awaiting values from Parser. */
    MarkCommand() {
        super("mark");
    }

    /**
     * Validates the task number before marking and persisting the selected task.
     *
     * @param tasks task collection to update.
     * @param ui user interface through which confirmation is shown.
     * @param storage persistent storage to update.
     * @throws ZuccException if the task cannot be marked or persisted.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws ZuccException {
        int taskIndex = parseTaskIndex();
        Task markedTask = tasks.mark(taskIndex);
        storage.saveTasks(tasks);
        ui.showMessage("Nice! I've marked this task as done:\n  " + markedTask);
    }
}
