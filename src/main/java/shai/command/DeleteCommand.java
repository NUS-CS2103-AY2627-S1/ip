package shai.command;

import shai.exception.ShaiException;
import shai.storage.Storage;
import shai.task.Task;
import shai.task.TaskList;
import shai.ui.Ui;

/**
 * A command that removes one task from the task list.
 */
public class DeleteCommand extends Command {
    private final int taskIndex;

    /** Creates a delete command for a zero-based task index. */
    public DeleteCommand(int taskIndex) {
        this.taskIndex = taskIndex;
    }

    /** Removes the selected task, reports the new count, and persists the list. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws ShaiException {
        Task task = tasks.remove(taskIndex);
        try {
            storage.saveTasks(tasks);
        } catch (ShaiException exception) {
            tasks.add(taskIndex, task);
            throw exception;
        }
        ui.showDeleted(task, tasks.size());
    }

    /** Returns whether deleting this task requires a storage write. */
    @Override
    public boolean requiresStorageWrite() {
        return true;
    }
}
