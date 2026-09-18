package rem.command;

import java.io.IOException;

import rem.exception.InvalidTaskNumberException;
import rem.storage.Storage;
import rem.task.Task;
import rem.task.TaskList;
import rem.ui.Ui;

/**
 * Removes a task from the task list.
 */
public class DeleteCommand extends Command {
    private final int taskNumber;

    /**
     * Creates a command for a selected task.
     *
     * @param taskNumber One-based number of the task to delete.
     */
    public DeleteCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Deletes the task, saves the task list, and displays the result.
     *
     * @param tasks Tasks managed by Rem.
     * @param ui User interface used to display the result.
     * @param storage Storage used to save the updated task list.
     * @throws IOException If the updated task list cannot be saved.
     * @throws InvalidTaskNumberException If the task number does not exist.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage)
            throws IOException, InvalidTaskNumberException {
        Task removedTask = tasks.delete(taskNumber);
        storage.saveTasks(tasks.getTasks());
        ui.showMessages("Don't need this one? Okay. Removed:", removedTask.toString(),
                "Now we are only left with " + tasks.size()
                        + (tasks.size() == 1 ? " task" : " tasks") + " in the list.");
    }
}
