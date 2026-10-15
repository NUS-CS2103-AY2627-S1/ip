package shai.command;

import shai.exception.ShaiException;
import shai.storage.Storage;
import shai.task.Task;
import shai.task.TaskList;
import shai.ui.Ui;

/**
 * A command that adds a task to the task list.
 */
public class AddCommand extends Command {
    private final Task task;

    /** Creates an add command for the supplied task. */
    public AddCommand(Task task) {
        this.task = task;
    }

    /** Adds the task, displays the updated count, and persists the list. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws ShaiException {
        tasks.add(task);
        try {
            storage.saveTasks(tasks);
        } catch (ShaiException exception) {
            tasks.remove(tasks.size() - 1);
            throw exception;
        }
        ui.showAdded(task, tasks.size());
    }

    /** Returns whether adding this task requires a storage write. */
    @Override
    public boolean requiresStorageWrite() {
        return true;
    }
}
