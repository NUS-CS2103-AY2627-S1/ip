package daddy.command;

import daddy.exception.DaddyException;
import daddy.storage.Storage;
import daddy.task.Task;
import daddy.task.TaskList;
import daddy.ui.Ui;

/**
 * Adds one task to the task list.
 */
public class AddCommand extends Command {
    /** Stores the task to add when this command executes. */
    private final Task task;

    /**
     * Creates a command that adds the supplied task.
     *
     * @param task the task to add
     */
    public AddCommand(Task task) {
        this.task = task;
    }

    /** Adds, saves, and displays the new task. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws DaddyException {
        if (tasks.containsEquivalentTask(task)) {
            throw new DaddyException("That task is already in the list, my friend. "
                    + "Complete it or delete it before adding it again.");
        }
        tasks.add(task);
        try {
            storage.save(tasks);
        } catch (DaddyException exception) {
            tasks.removeTaskAt(tasks.size() - 1);
            throw exception;
        }
        ui.showTaskAdded(task, tasks.size());
    }
}
