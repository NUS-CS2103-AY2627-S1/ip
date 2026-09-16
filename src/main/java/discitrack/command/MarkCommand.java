package discitrack.command;

import java.io.IOException;

import discitrack.exception.DisciTrackException;
import discitrack.exception.SaveException;
import discitrack.storage.Storage;
import discitrack.task.Task;
import discitrack.task.TaskList;
import discitrack.ui.Ui;

/**
 * Marks an existing task as done.
 */
public class MarkCommand extends Command {
    private final int taskNumber;

    /**
     * Creates a mark command for the given task number.
     *
     * @param taskNumber the one-based number of the task to mark.
     */
    public MarkCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Marks the task as done, saves the updated list, and shows the marked task.
     *
     * @param tasks the task list containing the task.
     * @param ui the UI used to show the result.
     * @param storage the storage used to save the updated list.
     * @throws DisciTrackException if the task number is invalid.
     * @throws IOException if saving the updated list fails.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws DisciTrackException, IOException {
        Task task = tasks.get(taskNumber);
        if (task.isDone()) {
            ui.showError("Task " + taskNumber + " is already completed, champ! No changes were needed.");
            return;
        }
        task.markAsDone();
        try {
            storage.save(tasks.asList());
        } catch (IOException e) {
            task.markAsUndone();
            throw new SaveException(e);
        }
        ui.showTaskMarked(task);
    }
}
