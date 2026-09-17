package crystal.command;

import crystal.exception.CrystalException;
import crystal.storage.Storage;
import crystal.task.Task;
import crystal.task.TaskList;
import crystal.ui.Ui;

/**
 * Marks one task as not completed and persists the changed status.
 */
public final class UnmarkCommand extends TaskIndexCommand {
    /**
     * Creates an unmark command for a zero-based task index.
     *
     * @param taskIndex zero-based task index.
     */
    public UnmarkCommand(int taskIndex) {
        super(taskIndex);
    }

    /**
     * Unmarks and saves the target task unless it is already not completed.
     *
     * @param tasks task list containing the target.
     * @param ui console interface used to present the result.
     * @param storage persistence service for status changes.
     * @throws CrystalException if the task does not exist or saving fails.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws CrystalException {
        Task task = getTask(tasks);
        if (!task.isDone()) {
            ui.showTaskAlreadyNotDone(task);
            return;
        }

        task.markAsNotDone();
        try {
            storage.saveTasks(tasks.getTasks());
        } catch (CrystalException exception) {
            task.markAsDone();
            throw new CrystalException(exception.getMessage());
        }
        ui.showTaskMarkedNotDone(task);
    }
}
