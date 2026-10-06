package caesar.command;

import caesar.exception.CaesarException;
import caesar.storage.Storage;
import caesar.task.Task;
import caesar.task.TaskList;
import caesar.ui.Ui;

/** Adds one task to the task list and persists the updated list. */
public class AddCommand extends Command {
    /** The task that will be added when this command executes. */
    private final Task task;

    /** Creates an add command for the supplied task. */
    public AddCommand(Task task) {
        this.task = task;
    }

    /** Adds the task, rolls back on a save failure, and reports success. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws CaesarException {
        int originalSize = tasks.size();
        tasks.add(task);
        // Adding a task must append exactly one item to the list.
        assert tasks.size() == originalSize + 1 : "Adding a task must increase the list size by one";
        try {
            storage.save(tasks);
        } catch (CaesarException exception) {
            tasks.delete(tasks.size());
            // A failed save must restore the list to its pre-command state.
            assert tasks.size() == originalSize : "A failed add must restore the original list size";
            throw exception;
        }
        ui.showTaskAdded(task, tasks);
    }
}
