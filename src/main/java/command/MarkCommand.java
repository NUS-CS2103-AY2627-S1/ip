package puyo.command;

import puyo.PuyoException;
import puyo.storage.Storage;
import puyo.task.Task;
import puyo.task.TaskList;
import puyo.ui.Ui;

/**
 * Represents a command to mark a task as completed.
 */
public class MarkCommand extends Command {

    private final int index;

    /**
     * Constructs a {@code MarkCommand} with the target task index.
     *
     * @param index The zero-based index of the task to be marked as done.
     */
    public MarkCommand(int index) {
        this.index = index;
    }

    /**
     * Executes the command by marking the task as done, displaying confirmation,
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
        if (task.getDone()) {
            ui.showMessage(" You indeed have done this task!");
        } else {
            task.markDone();
            ui.showMessage(" Amazing! Don't forget to take a rest!");
            ui.showMessage(" " + task);
            storage.save(tasks);
        }
    }
}
