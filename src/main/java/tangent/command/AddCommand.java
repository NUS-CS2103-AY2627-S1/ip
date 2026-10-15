package tangent.command;

import tangent.exception.TangentException;
import tangent.storage.Storage;
import tangent.task.Task;
import tangent.task.TaskList;
import tangent.ui.Ui;

/** Adds a task to the task list and saves it. */
public class AddCommand extends Command {

    /** Task to be added. */
    private final Task task;

    /**
     * Creates a command that adds the supplied task.
     */
    public AddCommand(Task task) {
        this.task = task;
    }

    /**
     * Adds a task to {@code tasks} and persists the updated list in {@code storage}.
     *
     * @throws TangentException if the data file cannot be written to.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws TangentException {
        tasks.add(task);
        try {
            storage.save(tasks.toList());
        } catch (TangentException e) {
            tasks.removeLast();
            throw e;
        }
        tasks.resetDisplayedIndexes();
        ui.showTaskAdded(task, tasks);
    }
}
