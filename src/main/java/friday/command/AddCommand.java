package friday.command;

import friday.exception.FridayException;
import friday.model.Task;
import friday.model.TaskList;
import friday.storage.Storage;
import friday.ui.Ui;

/**
 * Adds a parsed task to the task list.
 */
public class AddCommand extends Command {
    private final Task task;

    /**
     * Creates an add command.
     *
     * @param task Task to add.
     */
    public AddCommand(Task task) {
        this.task = task;
    }

    /**
     * Returns the task this command adds.
     *
     * @return The task supplied when the command was created.
     */
    public Task getTask() {
        return task;
    }

    /**
     * Adds the stored task and reports the updated list to the user.
     *
     * @param tasks Current task list to modify.
     * @param ui UI used to display the confirmation.
     * @param storage Storage available to the command; saving is coordinated by Friday.
     */
    @Override
    public void execute(TaskList tasks, TaskList archivedTasks, Ui ui, Storage storage, Storage archiveStorage)
            throws FridayException {
        if (tasks.containsEquivalent(task) || archivedTasks.containsEquivalent(task)) {
            throw new FridayException("A task with those details already exists.");
        }
        tasks.add(task);
        ui.showTaskAdded(task, tasks.size());
    }

    /**
     * Indicates that executing this command changes persisted state.
     *
     * @return true because a task is added.
     */
    @Override
    public boolean changesTasks() {
        return true;
    }
}
