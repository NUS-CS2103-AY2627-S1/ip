package duke.command;

import duke.exception.DukeException;
import duke.storage.Storage;
import duke.task.Task;
import duke.task.TaskList;
import duke.ui.Ui;

/**
 * Represents a command to delete a task from the task list.
 */
public class DeleteCommand extends Command {
    private final int targetIndex;

    /**
     * Constructs a DeleteCommand for the task at the specified index.
     *
     * @param targetIndex The 0-based index of the task to delete.
     */
    public DeleteCommand(int targetIndex) {
        this.targetIndex = targetIndex;
    }

    /**
     * Executes the delete command by removing the specified task, saving changes, and notifying the user.
     *
     * @param tasks   The task list.
     * @param ui      The user interface.
     * @param storage The storage handler.
     * @throws DukeException If the task index is invalid or saving fails.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws DukeException {
        tasks.saveSnapshot();
        Task removedTask = tasks.deleteTask(targetIndex);
        storage.save(tasks);
        ui.showTaskDeleted(removedTask, tasks.size());
    }
}
