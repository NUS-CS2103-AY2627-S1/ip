package puyo.command;

import puyo.PuyoException;
import puyo.storage.Storage;
import puyo.task.Task;
import puyo.task.TaskList;
import puyo.ui.Ui;

/**
 * Represents a command to delete a task from the task list.
 */
public class DeleteCommand extends Command {

    private final int index;

    /**
     * Constructs a {@code DeleteCommand} with the target task index.
     *
     * @param index The zero-based index of the task to be deleted.
     */
    public DeleteCommand(int index) {
        this.index = index;
    }

    /**
     * Executes the command by removing the specified task, displaying confirmation,
     * and saving the updated list to storage.
     *
     * @param tasks The list of tasks.
     * @param ui The user interface for interaction.
     * @param storage The storage handler for saving tasks.
     * @throws PuyoException If the provided task index is invalid or an error occurs during saving.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws PuyoException {
        if (index < 0 || index >= tasks.size()) {
            throw new PuyoException("Invalid task number.");
        }
        Task removed = tasks.remove(index);
        ui.showMessage(" Noted. I've removed this task:");
        ui.showMessage("   " + removed);
        ui.showMessage(" Now you have " + tasks.size() + " tasks in the list.");
        storage.save(tasks);
    }
}
