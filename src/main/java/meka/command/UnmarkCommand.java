package meka.command;

import java.io.IOException;

import meka.exception.MekaException;
import meka.storage.Storage;
import meka.task.Task;
import meka.task.TaskList;
import meka.ui.Ui;

/**
 * Marks a numbered task as incomplete.
 */
public class UnmarkCommand extends Command {
    /** One-based number of the task to unmark. */
    private final int taskNumber;

    /**
     * Creates a command that unmarks the specified task.
     *
     * @param taskNumber one-based task number.
     */
    public UnmarkCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Unmarks and saves the task, then shows confirmation.
     *
     * @param tasks task list containing the task.
     * @param ui user interface through which confirmation is displayed.
     * @param storage storage component used to persist the change.
     * @throws MekaException if the task number does not exist.
     * @throws IOException if the updated task list cannot be saved.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage)
            throws MekaException, IOException {
        Task task = tasks.get(taskNumber);
        task.unmark();
        storage.save(tasks);
        ui.showTaskUnmarked(task);
    }
}
