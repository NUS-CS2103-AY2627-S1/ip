package edith.command;

import edith.EdithException;
import edith.Storage;
import edith.Ui;
import edith.task.Task;
import edith.task.TaskList;

/** Adds a task and persists the updated list. */
public class AddCommand extends Command {
    private final Task task;
    /**
     * Creates an add command for the given task.
     *
     * @param task task to add
     */
    public AddCommand(Task task) {
        this.task = task;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws EdithException {
        if (tasks.hasDuplicate(task)) {
            throw new EdithException("OOPS!!! An identical task is already in your list.");
        }
        tasks.add(task);
        try {
            storage.save(tasks);
        } catch (EdithException error) {
            tasks.remove(tasks.size() - 1);
            throw error;
        }
        ui.showAddedTask(task, tasks.size());
    }
}
