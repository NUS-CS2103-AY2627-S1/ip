package duke.command;

import duke.exception.DukeException;
import duke.storage.Storage;
import duke.task.Task;
import duke.task.TaskList;
import duke.ui.Ui;

/**
 * Represents a command to mark a task as completed.
 */
public class MarkCommand extends Command {
    private final int targetIndex;

    /**
     * Constructs a MarkCommand for the task at the specified index.
     *
     * @param targetIndex The 0-based index of the task to mark as done.
     */
    public MarkCommand(int targetIndex) {
        this.targetIndex = targetIndex;
    }

    /**
     * Executes the mark command by marking the task as done, saving changes, and notifying the user.
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
        task.markAsDone();
        storage.save(tasks);
        ui.showTaskMarked(task);
    }
}
