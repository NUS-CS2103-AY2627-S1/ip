package duke.command;

import duke.exception.DukeException;
import duke.storage.Storage;
import duke.task.Task;
import duke.task.TaskList;
import duke.ui.Ui;

/**
 * Represents a command to mark a task as not completed yet.
 */
public class UnmarkCommand extends Command {
    private final int targetIndex;

    /**
     * Constructs an UnmarkCommand for the task at the specified index.
     *
     * @param targetIndex The 0-based index of the task to mark as not done.
     */
    public UnmarkCommand(int targetIndex) {
        this.targetIndex = targetIndex;
    }

    /**
     * Executes the unmark command by marking the task as not done yet, saving changes, and notifying the user.
     *
     * @param tasks   The task list.
     * @param ui      The user interface.
     * @param storage The storage handler.
     * @throws DukeException If the task index is invalid or saving fails.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws DukeException {
        tasks.saveSnapshot();
        Task task = tasks.getTask(targetIndex);
        task.markAsNotDone();
        storage.save(tasks);
        ui.showTaskUnmarked(task);
    }
}
