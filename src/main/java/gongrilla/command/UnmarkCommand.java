package gongrilla.command;

import java.io.IOException;

import gongrilla.exception.GongrillaException;
import gongrilla.storage.Storage;
import gongrilla.task.Task;
import gongrilla.task.TaskList;
import gongrilla.ui.Ui;

/**
 * Marks one task as incomplete.
 */
public class UnmarkCommand extends Command {
    private final int index;

    /**
     * Creates a command that marks one task incomplete.
     *
     * @param index zero-based index of the task.
     */
    public UnmarkCommand(int index) {
        this.index = index;
    }

    /**
     * Marks the task incomplete, records the change, and displays the result.
     *
     * @param tasks task list to modify.
     * @param ui user interface used to display the unmarked task.
     * @param storage storage used to persist the state change.
     * @throws GongrillaException if the index does not identify a task.
     * @throws IOException if the state change cannot be saved.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage)
            throws GongrillaException, IOException {
        Task task = getValidatedTask(tasks, index);
        if (task.isDone()) {
            storage.appendUnmark(index);
            task = tasks.unmark(index);
        }
        ui.showUnmarkedTask(task);
    }
}
