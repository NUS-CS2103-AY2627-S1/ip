package meka.command;

import java.io.IOException;

import meka.exception.MekaException;
import meka.storage.Storage;
import meka.task.Task;
import meka.task.TaskList;
import meka.ui.Ui;

/**
 * Deletes a numbered task from the task list.
 */
public class DeleteCommand extends Command {
    /** One-based number of the task to delete. */
    private final int taskNumber;

    /**
     * Creates a command that deletes the specified task.
     *
     * @param taskNumber one-based task number.
     */
    public DeleteCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Deletes and saves the task, then shows the updated task count.
     *
     * @param tasks task list to modify.
     * @param ui user interface through which confirmation is displayed.
     * @param storage storage component used to persist the change.
     * @throws MekaException if the task number does not exist.
     * @throws IOException if the updated task list cannot be saved.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage)
            throws MekaException, IOException {
        Task task = tasks.delete(taskNumber);
        storage.save(tasks);
        ui.showTaskDeleted(task, tasks.size());
    }
}
