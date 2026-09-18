package habpyduck.command;

import habpyduck.HabpyDuckException;
import habpyduck.storage.Storage;
import habpyduck.task.Task;
import habpyduck.task.TaskList;
import habpyduck.ui.Ui;

/**
 * Adds one task to the task list.
 */
public class AddCommand extends Command {
    private final Task task;

    /**
     * Creates a command that adds the given task.
     *
     * @param task the task to add.
     */
    public AddCommand(Task task) {
        this.task = task;
    }

    /**
     * Adds the task, saves the updated list, and reports the added task.
     *
     * @param tasks the task list to update.
     * @param ui the UI used to show command results.
     * @param storage the storage used to save tasks.
     * @throws HabpyDuckException if saving fails.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws HabpyDuckException {
        tasks.add(task);
        try {
            storage.saveTasks(tasks.asList());
        } catch (HabpyDuckException e) {
            assert tasks.size() > 0 : "Added task should still be available for rollback";
            tasks.removeLast();
            throw e;
        }
        ui.showTaskAdded(tasks.get(tasks.size() - 1), tasks.size());
    }
}
