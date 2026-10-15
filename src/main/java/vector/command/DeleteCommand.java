package vector.command;

import vector.VectorException;
import vector.storage.Storage;
import vector.task.Task;
import vector.task.TaskList;
import vector.ui.Ui;

/**
 * Represents a command to delete a task from the task list.
 */
public class DeleteCommand extends Command {
    private int index;

    /**
     * Constructs a DeleteCommand to remove a task at the specified index.
     *
     * @param index The zero-based index of the task to be deleted.
     */
    public DeleteCommand(int index) {
        this.index = index;
    }

    /**
     * Executes the command, removing the task from the list, saving to storage, and displaying a success message.
     *
     * @param tasks   The task list to operate on.
     * @param ui      The user interface for displaying messages.
     * @return The response string.
     * @throws VectorException If the index is invalid.
     */
    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage) throws VectorException {
        try {
            Task removedTask = tasks.remove(index);
            storage.save(tasks.getTasks());
            return "Task erased from memory banks:\n  " + removedTask.toString()
                    + "\nNow you have " + tasks.size() + " tasks in the matrix.";
        } catch (IndexOutOfBoundsException e) {
            throw new VectorException("That task number does not exist in your list.");
        }
    }
}
