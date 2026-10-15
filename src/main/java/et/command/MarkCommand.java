package et.command;

import et.exception.ETException;
import et.storage.Storage;
import et.task.Task;
import et.task.TaskList;
import et.ui.Ui;

/**
 * Marks one task as completed.
 */
public class MarkCommand extends TaskIndexCommand {
    /**
     * Creates a command that marks one task as completed.
     *
     * @param taskIndex the zero-based index of the task to mark
     */
    public MarkCommand(int taskIndex) {
        super(taskIndex);
    }

    /** {@inheritDoc} */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws ETException {
        Task task = getTask(tasks);
        task.markAsDone();
        saveTasks(tasks, ui, storage);
        ui.showTaskMarked(task);
    }
}
