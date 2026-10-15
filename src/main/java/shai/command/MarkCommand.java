package shai.command;

import shai.exception.ShaiException;
import shai.storage.Storage;
import shai.task.Task;
import shai.task.TaskList;
import shai.ui.Ui;

/**
 * A command that marks one task as done.
 */
public class MarkCommand extends Command {
    private final int taskIndex;

    /** Creates a mark command for a zero-based task index. */
    public MarkCommand(int taskIndex) {
        this.taskIndex = taskIndex;
    }

    /** Marks the selected task, displays the result, and persists the list. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws ShaiException {
        Task task = tasks.get(taskIndex);
        boolean wasDone = task.isDone();
        task.markAsDone();
        try {
            storage.saveTasks(tasks);
        } catch (ShaiException exception) {
            if (!wasDone) {
                task.unmark();
            }
            throw exception;
        }
        ui.showMarked(task);
    }

    /** Returns whether marking this task requires a storage write. */
    @Override
    public boolean requiresStorageWrite() {
        return true;
    }
}
