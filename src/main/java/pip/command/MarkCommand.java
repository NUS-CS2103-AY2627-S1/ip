package pip.command;

import pip.exception.ParserException;
import pip.storage.Storage;
import pip.task.Task;
import pip.task.TaskList;
import pip.ui.Ui;


/**
 * Represents a command that marks a task as done or not done.
 */
public class MarkCommand extends Command {
    private final int index;
    private final boolean isDone;

    /**
     * Creates a MarkCommand that will set the completion status of the
     * task at the given zero-based index when executed.
     *
     * @param index The zero-based index of the task to mark.
     * @param isDone {@code true} to mark the task as done, {@code false} to mark it as not done.
     */
    public MarkCommand(int index, boolean isDone) {
        this.index = index;
        this.isDone = isDone;
    }

    /**
     * Updates the completion status of the task at this command's index,
     * shows a confirmation to the user, and persists the updated task
     * list to storage.
     *
     * @param tasks The task list containing the task to mark.
     * @param ui The UI used to confirm the change.
     * @param storage The storage used to persist the updated task list.
     * @throws ParserException If the index is out of range for the task list.
     * @throws Exception If an I/O error occurs while saving.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws Exception {
        if (index < 0 || index >= tasks.size()) {
            throw new ParserException("Sorry! Invalid task index!");
        }
        Task task = tasks.get(index);
        if (isDone) {
            task.markDone();
        } else {
            task.markUndone();
        }
        ui.showTaskMarked(task, isDone);
        storage.save(tasks);
    }
}
