package bob.command;

import bob.exception.BobException;
import bob.storage.Storage;
import bob.task.Task;
import bob.task.TaskList;
import bob.ui.Ui;

/**
 * Represents a command to delete a task from the task list.
 */
public class DeleteCommand extends Command {
    private final int taskId;

    /**
     * Constructs a DeleteCommand with the specified 1-based task ID.
     *
     * @param taskId the 1-based index of the task to delete
     */
    public DeleteCommand(int taskId) {
        this.taskId = taskId;
    }

    /**
     * Executes the delete command by removing the task at the specified 1-based
     * index,
     * saving the updated list to storage, and notifying the user.
     *
     * @param tasks   the list of tasks to remove from
     * @param ui      the user interface handler
     * @param storage the storage handler to persist the task list
     * @throws BobException if the taskId is out of bounds or saving fails
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage<TaskList> storage) throws BobException {
        assertExecutionDependencies(tasks, ui, storage);
        try {
            Task task = tasks.remove(taskId - 1);
            storage.save(tasks);
            ui.setTaskDeleted(task, tasks.size());
        } catch (IndexOutOfBoundsException e) {
            throw new BobException("Error: taskId out of bounds");
        }
    }
}
