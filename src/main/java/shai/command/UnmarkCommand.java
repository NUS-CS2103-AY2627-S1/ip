package shai.command;

import shai.exception.ShaiException;
import shai.storage.Storage;
import shai.task.Task;
import shai.task.TaskList;
import shai.ui.Ui;

/**
 * A command that marks one task as not done.
 */
public class UnmarkCommand extends Command {
    private final int taskIndex;

    /** Creates an unmark command for a zero-based task index. */
    public UnmarkCommand(int taskIndex) {
        this.taskIndex = taskIndex;
    }

    /** Unmarks the selected task, displays the result, and persists the list. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws ShaiException {
        Task task = tasks.get(taskIndex);
        boolean wasDone = task.isDone();
        task.unmark();
        try {
            storage.saveTasks(tasks);
        } catch (ShaiException exception) {
            if (wasDone) {
                task.markAsDone();
            }
            throw exception;
        }
        ui.showUnmarked(task);
    }

    /** Returns whether unmarking this task requires a storage write. */
    @Override
    public boolean requiresStorageWrite() {
        return true;
    }
}
