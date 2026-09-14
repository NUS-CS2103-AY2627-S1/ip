package nova.command;

import nova.NovaException;
import nova.Storage;
import nova.TaskList;
import nova.Ui;
import nova.task.Task;

/** Adds a new task to the list. */
public class AddCommand extends Command {
    private final Task task;

    /**
     * Creates a command that will add the given task.
     *
     * @param task the task to add
     */
    public AddCommand(Task task) {
        this.task = task;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws NovaException {
        int duplicate = tasks.positionOfDuplicate(task);
        if (duplicate > 0) {
            throw new NovaException("That is already task " + duplicate + " in your list.");
        }

        tasks.add(task);
        storage.save(tasks.asList());
        ui.showAdded(task, tasks.size());
    }
}
