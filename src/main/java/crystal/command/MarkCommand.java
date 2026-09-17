package crystal.command;

import crystal.exception.CrystalException;
import crystal.storage.Storage;
import crystal.task.Task;
import crystal.task.TaskList;
import crystal.ui.Ui;

/**
 * Marks one task as completed and persists the changed status.
 */
public final class MarkCommand extends TaskIndexCommand {
    /**
     * Creates a mark command for a zero-based task index.
     *
     * @param taskIndex zero-based task index.
     */
    public MarkCommand(int taskIndex) {
        super(taskIndex);
    }

    /**
     * Marks and saves the target task unless it is already completed.
     *
     * @param tasks task list containing the target.
     * @param ui console interface used to present the result.
     * @param storage persistence service for status changes.
     * @throws CrystalException if the task does not exist or saving fails.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws CrystalException {
        Task task = getTask(tasks);
        if (task.isDone()) {
            ui.showTaskAlreadyDone(task);
            return;
        }

        task.markAsDone();
        try {
            storage.saveTasks(tasks.getTasks());
        } catch (CrystalException exception) {
            task.markAsNotDone();
            throw new CrystalException(exception.getMessage());
        }
        ui.showTaskMarkedDone(task);
    }
}
