package duck.command;

import java.util.Objects;

import duck.DuckException;
import duck.storage.Storage;
import duck.task.Task;
import duck.task.TaskList;
import duck.ui.Ui;

/**
 * Adds a parsed task to the task list and persists the updated list.
 */
public class AddCommand extends Command {
    /** Task to add when this command is executed. */
    private final Task task;

    /**
     * Creates a command that adds the supplied task.
     *
     * @param task Parsed task to add.
     */
    public AddCommand(Task task) {
        this.task = Objects.requireNonNull(task, "Task to add cannot be null.");
    }

    /**
     * Adds and saves the task, rolling back the in-memory change if saving fails.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws DuckException {
        int duplicateTaskIndex = tasks.indexOfTaskWithSameDetails(this.task);
        if (duplicateTaskIndex >= 0) {
            throw new DuckException("That task already exists as task "
                    + (duplicateTaskIndex + 1) + ".");
        }

        int originalTaskCount = tasks.getTaskCount();
        tasks.add(this.task);
        assert tasks.getTaskCount() == originalTaskCount + 1
                && tasks.get(originalTaskCount) == this.task
                : "Added task must be appended to the task list.";

        try {
            storage.save(tasks.getTasksSnapshot());
        } catch (DuckException e) {
            tasks.delete(tasks.getTaskCount() - 1);
            throw e;
        }
        ui.showTaskAdded(this.task, tasks.getTaskCount());
    }
}
