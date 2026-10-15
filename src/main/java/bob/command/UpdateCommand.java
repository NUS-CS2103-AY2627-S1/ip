package bob.command;

import java.time.LocalDateTime;

import bob.exception.BobException;
import bob.storage.Storage;
import bob.task.Task;
import bob.task.TaskList;
import bob.ui.Ui;

/**
 * Represents a command to update details of an existing task in the task list.
 */
public class UpdateCommand extends Command {

    private final int taskId;
    private final String newName;
    private final LocalDateTime newDeadline;
    private final LocalDateTime newFrom;
    private final LocalDateTime newTo;

    /**
     * Constructs an UpdateCommand with the specified task ID and optional updated field values.
     *
     * @param taskId      the 1-based index of the task to update
     * @param newName     the new description, or null if unchanged
     * @param newDeadline the new deadline date-time, or null if unchanged
     * @param newFrom     the new start date-time, or null if unchanged
     * @param newTo       the new end date-time, or null if unchanged
     */
    public UpdateCommand(int taskId, String newName, LocalDateTime newDeadline,
            LocalDateTime newFrom, LocalDateTime newTo) {
        this.taskId = taskId;
        this.newName = newName;
        this.newDeadline = newDeadline;
        this.newFrom = newFrom;
        this.newTo = newTo;
    }

    /**
     * Executes the update command by delegating field updating to the task itself,
     * replacing the old task with the updated instance in the task list,
     * saving changes to storage, and updating the UI diff.
     *
     * @param tasks   the list of tasks
     * @param ui      the user interface handler
     * @param storage the storage handler to persist the task list
     * @throws BobException if taskId is out of bounds, incompatible fields are provided, or saving fails
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage<TaskList> storage) throws BobException {
        assertExecutionDependencies(tasks, ui, storage);

        Task oldTask;
        try {
            oldTask = tasks.get(this.taskId - 1);
        } catch (IndexOutOfBoundsException e) {
            throw new BobException("Error: taskId out of bounds");
        }

        Task updatedTask = oldTask.update(this.newName, this.newDeadline, this.newFrom, this.newTo);

        tasks.set(this.taskId - 1, updatedTask);
        storage.save(tasks);
        ui.setTaskUpdated(oldTask, updatedTask);
    }
}
