package nova.command;

import java.io.IOException;

import nova.exception.NovaException;
import nova.task.Task;
import nova.task.TaskList;
import nova.ui.Ui;

/**
 * Deletes a numbered task from the task list.
 */
public class DeleteCommand extends Command {
    private final int taskNumber;

    /**
     * Creates a command that deletes the given task number.
     *
     * @param taskNumber one-based number of the task to delete
     */
    public DeleteCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Deletes the numbered task, saves the updated task list, and displays the result.
     *
     * @param tasks task list to update
     * @param ui UI used to display the deleted task
     * @throws NovaException if the task number does not exist
     * @throws IOException if the updated task list cannot be saved
     */
    @Override
    public void execute(TaskList tasks, Ui ui) throws NovaException, IOException {
        Task deletedTask = tasks.delete(taskNumber);
        ui.showDeletedTask(deletedTask, tasks.size());
    }
}
