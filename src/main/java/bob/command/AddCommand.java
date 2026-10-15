package bob.command;

import bob.exception.BobException;
import bob.storage.Storage;
import bob.task.Task;
import bob.task.TaskList;
import bob.ui.Ui;

/**
 * Represents a command to add a task to the task list.
 */
public class AddCommand extends Command {
    private final Task task;

    /**
     * Constructs an AddCommand with the specified task to add.
     *
     * @param task the task to be added
     */
    public AddCommand(Task task) {
        assert task != null : "Task to add should not be null";
        this.task = task;
    }

    /**
     * Executes the add command by adding the task to the task list,
     * saving the updated list to storage, and displaying a confirmation to the
     * user.
     *
     * @param tasks   the list of tasks to add to
     * @param ui      the user interface handler
     * @param storage the storage handler to persist the task list
     * @throws BobException if an error occurs while saving to storage
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage<TaskList> storage) throws BobException {
        assertExecutionDependencies(tasks, ui, storage);
        tasks.add(this.task);
        storage.save(tasks);
        ui.setTaskAdded(this.task, tasks.size());
    }
}
