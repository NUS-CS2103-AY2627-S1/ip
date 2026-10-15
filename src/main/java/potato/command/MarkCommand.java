package potato.command;

import potato.exception.PotatoException;
import potato.storage.Storage;
import potato.task.Task;
import potato.tasklist.TaskList;
import potato.ui.Ui;

/**
 * Command to mark a task as completed or incomplete.
 */
public class MarkCommand extends Command {
    private final int index;
    private final boolean isDone;

    /**
     * Constructs a {@code MarkCommand} targeting the specified task index and completion status.
     *
     * @param index Zero-based target task index.
     * @param isDone {@code true} to mark as completed, or {@code false} to unmark as incomplete.
     */
    public MarkCommand(int index, boolean isDone) {
        this.index = index;
        this.isDone = isDone;
    }

    /**
     * Executes the mark or unmark operation on the task list and saves the updated tasks to storage.
     *
     * @param tasks Task list containing target task.
     * @param ui User interface context for displaying results.
     * @param storage Persistence layer for saving task list changes.
     * @throws PotatoException If target task index is out of bounds or file saving fails.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws PotatoException {
        Task task = tasks.get(index);
        if (isDone) {
            task.markAsDone();
            ui.showTaskMarked(task);
        } else {
            task.markAsUndone();
            ui.showTaskUnmarked(task);
        }
        storage.save(tasks.getTasks());
    }
}