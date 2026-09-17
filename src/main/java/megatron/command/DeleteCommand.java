package megatron.command;

import megatron.exception.MegatronException;
import megatron.exception.StorageException;
import megatron.storage.TaskStorage;
import megatron.task.Task;
import megatron.task.TaskList;
import megatron.ui.Ui;

/**
 * Deletes one task from the task list.
 */
public final class DeleteCommand extends Command {
    private final int taskNumber;

    /**
     * Creates a command for a one-based task number.
     *
     * @param taskNumber The one-based task number.
     */
    public DeleteCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Deletes, saves, and displays the selected task.
     *
     * @param tasks The current task list.
     * @param ui The user interface used for output.
     * @param storage The task storage used for persistence.
     * @throws MegatronException If the task number is invalid.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, TaskStorage storage) throws MegatronException {
        Task removedTask = tasks.removeTask(taskNumber);
        try {
            storage.save(tasks);
        } catch (StorageException exception) {
            tasks.add(removedTask);
            throw exception;
        }
        ui.showTaskDeleted(removedTask, tasks.size());
    }
}
