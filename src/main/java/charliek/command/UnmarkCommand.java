package charliek.command;

import java.util.OptionalInt;

import charliek.exception.TaskStorageException;
import charliek.model.Task;
import charliek.model.TaskList;
import charliek.storage.Storage;
import charliek.ui.Ui;

/**
 * Marks a selected task as not done and persists the updated list.
 */
public class UnmarkCommand extends Command {
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
     * Creates an unmark command.
     *
     * @param tasks the task list to update.
     * @param ui the UI used to show the result.
     * @param storage the storage used to persist the updated list.
     * @param taskNumberText the task number supplied by the user.
     */
    public UnmarkCommand(TaskList tasks, Ui ui, Storage storage, String taskNumberText) {
        this.tasks = tasks;
        this.ui = ui;
        this.storage = storage;
        this.taskNumberText = taskNumberText;
    }

    /**
     * Marks the selected task as not done, saves the updated list, and reports the result.
     *
     * @throws TaskStorageException if the updated task list cannot be saved.
     */
    @Override
    public void execute() throws TaskStorageException {
        OptionalInt taskIndex = parseTaskIndex(taskNumberText, tasks, ui);
        if (taskIndex.isEmpty()) {
            return;
        }

        Task task = tasks.get(taskIndex.getAsInt());
        if (!task.isDone()) {
            ui.showTaskAlreadyUnmarked(task);
            return;
        }

        task.markAsNotDone();
        saveTasksOrRollback(storage, tasks, task::markAsDone);
        ui.showTaskUnmarked(task);
    }
}
