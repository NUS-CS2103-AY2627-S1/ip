package vector.command;

import vector.VectorException;
import vector.storage.Storage;
import vector.task.TaskList;
import vector.ui.Ui;

/**
 * Represents a command to mark a task as not done.
 */
public class UnmarkCommand extends Command {
    private int index;

    /**
     * Constructs an UnmarkCommand to unmark the task at the specified index.
     *
     * @param index The zero-based index of the task to unmark.
     */
    public UnmarkCommand(int index) {
        this.index = index;
    }

    /**
     * Executes the command, marking the task as not done, saving to storage, and displaying a success message.
     *
     * @param tasks   The task list to operate on.
     * @param ui      The user interface for displaying messages.
     * @return The response string.
     * @throws VectorException If the index is invalid.
     */
    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage) throws VectorException {
        try {
            tasks.get(index).unmarkAsDone();
            storage.save(tasks.getTasks());
            return "Task execution reverted. Status updated to: PENDING:\n  " + tasks.get(index).toString();
        } catch (IndexOutOfBoundsException e) {
            throw new VectorException("That task number does not exist in your list.");
        }
    }
}
