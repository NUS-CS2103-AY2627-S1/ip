package greenchonk.command;

import greenchonk.exception.GreenChonkException;
import greenchonk.storage.Storage;
import greenchonk.task.Task;
import greenchonk.task.TaskList;
import greenchonk.ui.Ui;

/**
 * Adds a task to the task list.
 */
public class AddCommand extends Command {
    private final Task task;

    /**
     * Creates a command that adds the specified task.
     *
     * @param task the task to add.
     */
    public AddCommand(Task task) {
        this.task = task;
    }

    /**
     * Adds the configured task and persists the updated task list.
     * Restores the original list if persistence fails.
     *
     * @param tasks the task list to update
     * @param ui the UI that reports the added task
     * @param storage the storage to which the updated list is saved
     * @throws GreenChonkException if the updated task list cannot be saved
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage)
            throws GreenChonkException {
        if (tasks.containsEquivalent(task)) {
            throw new GreenChonkException(
                    "Green Chonk is already carrying a task with those details.");
        }

        tasks.add(task);
        try {
            storage.save(tasks);
        } catch (GreenChonkException exception) {
            tasks.delete(tasks.size() - 1);
            throw exception;
        }
        ui.showTaskAdded(task, tasks.size());
    }
}
