package benjamin.command;

import benjamin.BenjaminException;
import benjamin.storage.Storage;
import benjamin.task.Task;
import benjamin.task.TaskList;
import benjamin.ui.Ui;

/** Adds one new task to the list. */
public class AddCommand extends Command {
    private final Task task;

    /**
     * Creates a command that will add the given task.
     *
     * @param task the task built from what the user typed.
     */
    public AddCommand(Task task) {
        this.task = task;
    }

    /**
     * Adds the task, reports it, and saves the longer list.
     *
     * @throws BenjaminException if the list cannot be saved.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws BenjaminException {
        if (tasks.containsDuplicateOf(task)) {
            throw new BenjaminException("You already have that exact task on the list.");
        }

        tasks.add(task);
        ui.showAdded(task, tasks.size());
        storage.save(tasks);
    }
}
