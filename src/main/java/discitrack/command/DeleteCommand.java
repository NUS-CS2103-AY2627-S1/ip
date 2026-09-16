package discitrack.command;

import java.io.IOException;

import discitrack.exception.DisciTrackException;
import discitrack.exception.SaveException;
import discitrack.storage.Storage;
import discitrack.task.Task;
import discitrack.task.TaskList;
import discitrack.ui.Ui;

/**
 * Deletes an existing task from the task list.
 */
public class DeleteCommand extends Command {
    private final int taskNumber;

    /**
     * Creates a delete command for the given task number.
     *
     * @param taskNumber the one-based number of the task to delete.
     */
    public DeleteCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Deletes the task, saves the updated list, and shows the removed task.
     *
     * @param tasks the task list to delete from.
     * @param ui the UI used to show the result.
     * @param storage the storage used to save the updated list.
     * @throws DisciTrackException if the task number is invalid.
     * @throws IOException if saving the updated list fails.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws DisciTrackException, IOException {
        Task removedTask = tasks.delete(taskNumber);
        try {
            storage.save(tasks.asList());
        } catch (IOException e) {
            tasks.asList().add(taskNumber - 1, removedTask);
            throw new SaveException(e);
        }
        ui.showTaskDeleted(removedTask, tasks.size());
    }
}
