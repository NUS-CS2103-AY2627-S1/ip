package discitrack.command;

import java.io.IOException;

import discitrack.exception.DisciTrackException;
import discitrack.exception.SaveException;
import discitrack.storage.Storage;
import discitrack.task.Task;
import discitrack.task.TaskList;
import discitrack.ui.Ui;


/**
 * Adds a new task to the task list.
 */
public class AddCommand extends Command {
    private final Task task;

    /**
     * Creates an add command for the given task.
     *
     * @param task the task to add.
     */
    public AddCommand(Task task) {
        this.task = task;
    }

    /**
     * Adds the task, saves the updated list, and shows the added task.
     *
     * @param tasks the task list to add to.
     * @param ui the UI used to show the result.
     * @param storage the storage used to save the updated list.
     * @throws IOException if saving the updated list fails.
     * @throws DisciTrackException if a task is rolled back after a failed save.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws IOException, DisciTrackException {
        int originalTaskCount = tasks.size();

        tasks.add(task);

        assert tasks.size() == originalTaskCount + 1
                : "Adding one task must increase the task count by one.";

        try {
            storage.save(tasks.asList());
        } catch (IOException e) {
            tasks.delete(tasks.size());
            throw new SaveException(e);
        }
        ui.showTaskAdded(task, tasks.size());
    }
}
