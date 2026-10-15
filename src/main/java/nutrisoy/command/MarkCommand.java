package nutrisoy.command;

import nutrisoy.exception.DukeException;
import nutrisoy.task.Task;
import nutrisoy.task.TaskList;
import nutrisoy.ui.Ui;

/**
 * Marks a task identified by a user-provided index as complete.
 */
public class MarkCommand extends Command {
    private final String indexString;

    /**
     * Creates a command with the task index to mark.
     *
     * @param indexString one-based index supplied by the user
     */
    public MarkCommand(String indexString) {
        assert indexString != null : "Task index must not be null";
        this.indexString = indexString;
    }

    /** {@inheritDoc} */
    @Override
    public void execute(TaskList tasks, Ui ui) throws DukeException {
        assert tasks != null && ui != null : "Command collaborators must not be null";
        int index = parseTaskIndex(indexString, tasks,
                "Please specify the task number to mark as done. Use: mark [index]");
        Task task = tasks.get(index);
        if (task.isDone()) {
            throw new DukeException("That task is already marked as done.");
        }
        task.markAsDone();
        ui.showTaskMarked(task);
    }
}
