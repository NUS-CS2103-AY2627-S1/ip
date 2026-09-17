package megatron.command;

import megatron.exception.MegatronException;
import megatron.exception.StorageException;
import megatron.storage.TaskStorage;
import megatron.task.Task;
import megatron.task.TaskList;
import megatron.ui.Ui;

/**
 * Marks one task as not done.
 */
public final class UnmarkCommand extends Command {
    private final int taskNumber;

    /**
     * Creates a command for a one-based task number.
     *
     * @param taskNumber The one-based task number.
     */
    public UnmarkCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Unmarks, saves, and displays the selected task.
     *
     * @param tasks The current task list.
     * @param ui The user interface used for output.
     * @param storage The task storage used for persistence.
     * @throws MegatronException If the task number is invalid.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, TaskStorage storage) throws MegatronException {
        Task task = tasks.getTask(taskNumber);
        boolean wasDone = task.isDone();
        task.markAsNotDone();
        try {
            storage.save(tasks);
        } catch (StorageException exception) {
            if (wasDone) {
                task.markAsDone();
            }
            throw exception;
        }
        ui.showTaskMarked(task, false);
    }
}
