package matthew.command;

import matthew.exception.MatthewException;
import matthew.storage.Storage;
import matthew.task.Task;
import matthew.task.TaskList;
import matthew.ui.Ui;

/** Deletes a task from the task list and saves the updated list. */
public class DeleteCommand extends Command {
    private final int taskNumber;

    /**
     * Creates a command that deletes the task with the specified number.
     *
     * @param taskNumber One-based number of the task to delete.
     */
    public DeleteCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Deletes the task, persists the task list, and displays a confirmation.
     *
     * @param tasks Task list to modify.
     * @param ui User interface used to display the result.
     * @param storage Storage used to persist the updated list.
     * @throws MatthewException If the task number is invalid or the list cannot be saved.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage)
            throws MatthewException {
        Task removedTask = tasks.delete(taskNumber);
        storage.save(tasks);
        ui.showDeletedTask(removedTask, tasks.size());
    }
}
