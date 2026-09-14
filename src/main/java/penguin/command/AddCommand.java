package penguin.command;

import penguin.exception.PenguinException;
import penguin.storage.Storage;
import penguin.task.Task;
import penguin.task.TaskList;
import penguin.task.TaskListSnapshot;
import penguin.ui.Ui;

/**
 * Represents a command that adds a task.
 */
public class AddCommand extends Command {
    /** Task to add. */
    private final Task task;

    /**
     * Creates an add command.
     *
     * @param newTask task to add.
     */
    public AddCommand(Task newTask) {
        this.task = newTask;
    }

    /**
     * Adds the task, persists the updated list, and reports the result.
     *
     * @param tasks task list to modify.
     * @param ui interface used for output.
     * @param storage storage used to persist the change.
     * @throws PenguinException if persistence fails.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws PenguinException {
        assert task != null : "Add command must contain a task";
        assert tasks != null : "Add command requires a task list";
        assert ui != null : "Add command requires a ui";
        assert storage != null : "Add command requires a storage";

        TaskListSnapshot previousState = tasks.createSnapshot();
        tasks.addTask(task);
        persistOrRestore(tasks, storage, previousState);
        ui.showMessage("Ice work! I have added '" + task + "' to your list of tasks."
                + " Now you have " + formatTaskCount(tasks.size()) + " in the list.");
    }
}
