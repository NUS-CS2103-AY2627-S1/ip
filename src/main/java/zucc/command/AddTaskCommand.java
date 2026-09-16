package zucc.command;

import zucc.ZuccException;
import zucc.storage.Storage;
import zucc.task.Task;
import zucc.task.TaskList;
import zucc.ui.Ui;

/**
 * Provides the shared execution sequence for commands that add one task.
 */
public abstract class AddTaskCommand extends Command {
    /**
     * Creates an add-task command with its accepted input shape.
     *
     * @param keyword user-entered keyword represented by this command.
     * @param allowedOptions named options accepted by this command.
     */
    protected AddTaskCommand(String keyword, String... allowedOptions) {
        super(keyword, allowedOptions);
    }

    /**
     * Validates the raw command values and creates the task to add.
     *
     * @return fully constructed task.
     * @throws ZuccException if required task data is missing or malformed.
     */
    protected abstract Task createTask() throws ZuccException;

    /**
     * Creates the task before performing any state change, then adds and persists it.
     *
     * @param tasks task collection to update.
     * @param ui user interface through which confirmation is shown.
     * @param storage persistent storage to update.
     * @throws ZuccException if task construction or persistence fails.
     */
    @Override
    public final void execute(TaskList tasks, Ui ui, Storage storage)
            throws ZuccException {
        Task task = createTask();
        tasks.add(task);
        storage.saveTasks(tasks);
        ui.showMessage("Got it. I've added this task:\n  "
                + task
                + "\nNow you have " + tasks.getTaskCount() + " tasks in the list.");
    }
}
