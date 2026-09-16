package turtley.command;

import turtley.model.Task;
import turtley.model.TaskList;
import turtley.storage.Storage;
import turtley.ui.Ui;

/**
 * Base class for commands that create and save one new task.
 */
public abstract class AddCommand extends Command {

    /**
     * Creates an add command.
     */
    protected AddCommand() {
    }

    /**
     * Creates the task represented by this command.
     *
     * @return the new task.
     */
    protected abstract Task createTask();

    /**
     * Creates, stores, and persists the task represented by this command.
     *
     * @param tasks the application's task list.
     * @param ui the application's user interface.
     * @param storage the application's persistence service.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        if (tasks.isFull()) {
            ui.showTaskListFull();
            return;
        }

        Task task = createTask();
        assert task != null : "An add command must create a task.";
        int previousTaskCount = tasks.size();
        tasks.add(task);
        assert tasks.size() == previousTaskCount + 1 : "Adding a task must increase the list size by one.";
        assert tasks.get(previousTaskCount) == task
                : "A newly added task must be appended for rollback to remove it correctly.";
        saveOrRollback(tasks, storage, () -> tasks.remove(tasks.size() - 1));
        ui.showTaskAdded(task, tasks.size());
    }
}
