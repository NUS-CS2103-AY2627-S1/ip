package aster.command;

import aster.exception.AsterException;
import aster.storage.Storage;
import aster.task.Task;
import aster.task.TaskList;
import aster.ui.Ui;

/**
 * Adds one task to the list.
 *
 * <p>The task itself is built by the {@code Parser}, which is what lets one command
 * cover todos, deadlines and events without repeating anything: by the time this
 * command exists, the difference between them is already settled.
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
     * Adds the task, reports it with the new number of tasks, and saves the list.
     *
     * @param tasks the task list to add to.
     * @param ui the user interface to report through.
     * @param storage the store to save the changed list to.
     * @throws AsterException if the changed list cannot be saved.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws AsterException {
        tasks.add(task);
        ui.showAdded(task, tasks.size());
        storage.save(tasks.asList());
    }
}
