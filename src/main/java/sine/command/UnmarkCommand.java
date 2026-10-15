package sine.command;

import java.io.IOException;

import sine.storage.Storage;
import sine.task.Task;
import sine.task.TaskList;
import sine.ui.Ui;

/**
 * Marks one task as not completed.
 */
public class UnmarkCommand extends Command {
    private final int taskIndex;

    /**
     * Creates a command that unmarks the task at a zero-based index.
     *
     * @param taskIndex Zero-based index of the task to unmark.
     */
    public UnmarkCommand(int taskIndex) {
        this.taskIndex = taskIndex;
    }

    /**
     * Marks the selected task as not done, saves the list, and shows the confirmation.
     *
     * @param tasks current task list
     * @param ui console user interface
     * @param storage task persistence service
     * @throws IOException if the updated task list cannot be saved
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws IOException {
        Task task = tasks.get(taskIndex);
        tasks.setDone(taskIndex, false);
        storage.save(tasks.getTasks());
        ui.showUnmarkedTask(task);
    }
}
