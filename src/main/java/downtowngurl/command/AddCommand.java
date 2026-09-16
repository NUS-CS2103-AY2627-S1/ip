package downtowngurl.command;

import downtowngurl.exception.DowntownGurlException;
import downtowngurl.storage.Storage;
import downtowngurl.task.Task;
import downtowngurl.task.TaskList;
import downtowngurl.ui.Ui;

/**
 * Represents the command that adds a new task.
 */
public class AddCommand extends Command {
    private final Task task;

    /**
     * Creates a command for adding the given task.
     *
     * @param task Task to add.
     */
    public AddCommand(Task task) {
        this.task = task;
    }

    /**
     * Adds the task, persists the updated list, and reports the new task count.
     *
     * @param tasks Current task list.
     * @param storage Storage helper used to save task changes.
     * @param ui UI helper used to show command results.
     * @throws DowntownGurlException If the updated task list cannot be saved.
     */
    @Override
    public void execute(TaskList tasks, Storage storage, Ui ui) throws DowntownGurlException {
        int originalSize = tasks.size();
        tasks.add(this.task);
        assert tasks.size() == originalSize + 1 : "Adding a task should increase the task count by one.";
        assert tasks.get(tasks.size() - 1) == this.task : "Added task should be appended to the end of the list.";
        try {
            storage.saveTasks(tasks);
        } catch (DowntownGurlException e) {
            tasks.removeLast();
            assert tasks.size() == originalSize : "Failed add should roll back the task count.";
            throw e;
        }
        ui.showAddedTask(this.task, tasks.size());
    }
}
