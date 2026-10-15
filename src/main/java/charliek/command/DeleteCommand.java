package charliek.command;

import java.util.OptionalInt;

import charliek.exception.TaskStorageException;
import charliek.model.Task;
import charliek.model.TaskList;
import charliek.storage.Storage;
import charliek.ui.Ui;

/**
 * Deletes a selected task and persists the updated list.
 */
public class DeleteCommand extends Command {
    /**
     * The task list to update.
     */
    private final TaskList tasks;

    /**
     * The UI used to show the result.
     */
    private final Ui ui;

    /**
     * The storage used to persist the updated list.
     */
    private final Storage storage;

    /**
     * The one-based task number supplied by the user.
     */
    private final String taskNumberText;

    /**
     * Creates a delete command.
     *
     * @param tasks the task list to update.
     * @param ui the UI used to show the result.
     * @param storage the storage used to persist the updated list.
     * @param taskNumberText the task number supplied by the user.
     */
    public DeleteCommand(TaskList tasks, Ui ui, Storage storage, String taskNumberText) {
        this.tasks = tasks;
        this.ui = ui;
        this.storage = storage;
        this.taskNumberText = taskNumberText;
    }

    /**
     * Deletes the selected task, saves the updated list, and reports the deletion.
     *
     * @throws TaskStorageException if the updated task list cannot be saved.
     */
    @Override
    public void execute() throws TaskStorageException {
        OptionalInt taskIndex = parseTaskIndex(taskNumberText, tasks, ui);
        if (taskIndex.isEmpty()) {
            return;
        }

        Task deletedTask = tasks.remove(taskIndex.getAsInt());
        saveTasksOrRollback(storage, tasks, () -> tasks.add(taskIndex.getAsInt(), deletedTask));

        ui.showTaskDeleted(deletedTask, tasks.size());
    }
}
