package bubu.command;

import java.io.UncheckedIOException;

import bubu.exception.BubuException;
import bubu.storage.Storage;
import bubu.task.Task;
import bubu.task.TaskList;
import bubu.ui.Ui;

/**
 * Base class for commands that create and save one task.
 */
public abstract class AddTaskCommand extends Command {
    private static final int LAST_ITEM_OFFSET = 1;

    /**
     * Adds a task, saves the updated list, and shows confirmation.
     *
     * @param task task to add.
     * @param tasks task list to update.
     * @param ui user interface for confirmation.
     * @param storage storage used to save the list.
     */
    protected void addTask(Task task, TaskList tasks, Ui ui, Storage storage) throws BubuException {
        tasks.add(task);
        try {
            storage.saveTasks(tasks.asList());
        } catch (UncheckedIOException exception) {
            tasks.remove(tasks.size() - LAST_ITEM_OFFSET);
            throw exception;
        }
        ui.showTaskAdded(task, tasks.size());
    }
}
