package vector.command;

import vector.VectorException;
import vector.storage.Storage;
import vector.task.TaskList;
import vector.ui.Ui;

/**
 * Represents a command to mark a task as done.
 */
public class MarkCommand extends Command {
    private int index;

    /**
     * Constructs a MarkCommand to mark the task at the specified index.
     *
     * @param index The zero-based index of the task to mark.
     */
    public MarkCommand(int index) {
        this.index = index;
    }

    /**
     * Executes the command, marking the task as done, saving to storage, and displaying a success message.
     *
     * @param tasks   The task list to operate on.
     * @param ui      The user interface for displaying messages.
     * @return The response string.
     * @throws VectorException If the index is invalid.
     */
    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage) throws VectorException {
        try {
            tasks.get(index).markAsDone();
            storage.save(tasks.getTasks());
            return "Task execution verified. Status updated to: COMPLETE:\n  " + tasks.get(index).toString();
        } catch (IndexOutOfBoundsException e) {
            throw new VectorException("That task number does not exist in your list.");
        }
    }
}
