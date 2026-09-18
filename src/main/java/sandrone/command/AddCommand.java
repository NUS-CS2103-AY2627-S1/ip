package sandrone.command;

import sandrone.SandroneException;
import sandrone.storage.Storage;
import sandrone.task.Task;
import sandrone.task.TaskList;
import sandrone.ui.Ui;

/**
 * Adds one parsed task to the task list.
 */
public class AddCommand extends Command {
    private final Task task;
    private final String commandText;
    /**
     * Creates a command that adds the supplied task.
     */
    public AddCommand(Task task, String commandText) {
        this.task = task;
        this.commandText = commandText;
    }

    /**
     * Adds the task, reports the result, and saves the updated list.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws SandroneException {
        tasks.addTask(task);
        ui.showTaskAdded(commandText, tasks.size());
        storage.save(tasks.getTasks());
    }
}
