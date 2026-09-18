package plana.command;

import java.util.List;

import plana.exception.PlanaException;
import plana.storage.Storage;
import plana.task.Task;
import plana.task.TaskList;
import plana.ui.Ui;

/**
 * Deletes a task and persists the updated task list.
 */
public class DeleteCommand extends Command {
    private final String taskNumber;

    /**
     * Creates a delete command for a user-facing task number.
     *
     * @param taskNumber the one-based task number entered by the user.
     */
    public DeleteCommand(String taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Deletes the selected task, saves the list, and displays the result.
     *
     * @param tasks the task list to update.
     * @param ui the user interface used for the response.
     * @param storage the storage collaborator used for persistence.
     * @throws PlanaException if the task number is invalid
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws PlanaException {
        List<Task> savedTasks = tasks.copyTasks();
        Task deletedTask = tasks.delete(taskNumber);
        if (!storage.saveTasks(tasks)) {
            tasks.restoreTasks(savedTasks);
            throw new PlanaException("Oops, I couldn't save that change."
                    + " Please check that the data folder is writable.");
        }
        ui.showTaskDeleted(deletedTask, tasks.size());
    }
}
