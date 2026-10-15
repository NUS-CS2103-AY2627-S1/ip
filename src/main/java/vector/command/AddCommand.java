package vector.command;

import vector.storage.Storage;
import vector.task.Task;
import vector.task.TaskList;
import vector.ui.Ui;

/**
 * Represents a command to add a new task to the task list.
 */
public class AddCommand extends Command {
    private Task task;

    /**
     * Constructs an AddCommand to add the specified task.
     *
     * @param task The task to be added.
     */
    public AddCommand(Task task) {
        this.task = task;
    }

    /**
     * Executes the command, adding the task to the list, saving to storage, and displaying a success message.
     *
     * @param tasks   The task list to operate on.
     * @param ui      The user interface for displaying messages.
     * @param storage The storage for saving updates.
     * @return The response string.
     */
    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage) throws vector.VectorException {
        tasks.add(task);
        storage.save(tasks.getTasks());
        return "Data logged. New task added to the matrix:\n  " + task.toString() + "\nNow you have "
                + tasks.size() + " tasks in the matrix.";
    }
}
