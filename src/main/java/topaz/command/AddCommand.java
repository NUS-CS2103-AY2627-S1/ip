package topaz.command;

import topaz.TopazException;
import topaz.storage.Storage;
import topaz.task.Task;
import topaz.task.TaskList;
import topaz.ui.Ui;

/**
 * Adds one task to the list and saves the updated task list.
 */
public class AddCommand extends Command {
    private final Task task;

    /**
     * Creates a command that adds the given task.
     *
     * @param task the task to add
     */
    public AddCommand(Task task) {
        this.task = task;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws TopazException {
        // Every AddCommand is constructed from a successfully parsed task.
        assert task != null : "An add command must contain a task.";
        if (tasks.containsDuplicate(task)) {
            throw new TopazException("That task already exists. Use list to find it.");
        }
        int originalSize = tasks.size();
        tasks.add(task);
        assert tasks.get(tasks.size() - 1) == task
                : "Adding a task must append the same task to the list.";
        try {
            storage.save(tasks.asList());
        } catch (TopazException exception) {
            tasks.remove(tasks.size() - 1);
            assert tasks.size() == originalSize
                    : "A failed save must restore the task list before the addition.";
            throw exception;
        }
        ui.showAddedTask(task, tasks.size());
    }
}
