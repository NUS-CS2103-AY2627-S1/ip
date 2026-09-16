package puyo.command;

import puyo.PuyoException;
import puyo.storage.Storage;
import puyo.task.Task;
import puyo.task.TaskList;
import puyo.ui.Ui;

/**
 * Represents a command to mark a task as not completed yet.
 */
public class UnmarkCommand extends Command {

    private final int index;

    /**
     * Constructs an {@code UnmarkCommand} with the target task index.
     *
     * @param index The zero-based index of the task to be unmarked.
     */
    public UnmarkCommand(int index) {
        this.index = index;
    }

    /**
     * Executes the command by unmarking the task, displaying confirmation,
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
        Task task = tasks.get(index);
        if (!task.getDone()) {
            ui.showMessage(" You indeed haven't done this task!");
        } else {
            task.unmark();
            ui.showMessage(" Don't forget to " + task.getName() + "!");
            ui.showMessage(" " + task);
            storage.save(tasks);
        }
    }
}
