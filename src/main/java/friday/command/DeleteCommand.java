package friday.command;

import friday.exception.FridayException;
import friday.model.Task;
import friday.model.TaskList;
import friday.storage.Storage;
import friday.ui.Ui;

/**
 * Removes a task from the task list.
 */
public class DeleteCommand extends Command {
    private final int taskNumber;
    private Task deletedTask;

    /**
     * Creates a delete command.
     *
     * @param taskNumber One-based number of the task to remove.
     */
    public DeleteCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Returns the task removed by the most recent successful execution.
     *
     * @return The removed task, or {@code null} before successful execution.
     */
    public Task getDeletedTask() {
        return deletedTask;
    }

    /**
     * Removes the requested task and reports the updated list to the user.
     *
     * @param tasks Current task list to modify.
     * @param ui UI used to display the confirmation.
     * @param storage Storage available to the command; saving is coordinated by Friday.
     * @throws FridayException If the task number is not valid.
     */
    @Override
    public void execute(TaskList tasks, TaskList archivedTasks, Ui ui, Storage storage,
            Storage archiveStorage) throws FridayException {
        Task task = getTask(tasks, taskNumber);
        tasks.remove(taskNumber - 1);
        deletedTask = task;
        ui.showTaskDeleted(task, tasks.size());
    }

    /**
     * Indicates that executing this command changes persisted state.
     *
     * @return true because a task is removed.
     */
    @Override
    public boolean changesTasks() {
        return true;
    }
}
