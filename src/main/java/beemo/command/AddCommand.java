package beemo.command;

import beemo.BeemoException;
import beemo.storage.Storage;
import beemo.task.Task;
import beemo.task.TaskList;
import beemo.ui.Ui;

/**
 * Adds a task to the task list.
 */
public class AddCommand extends Command {
    private final Task task;

    /**
     * Creates a command that will add the supplied task.
     *
     * @param task Task to add.
     */
    public AddCommand(Task task) {
        assert task != null : "Task to add must not be null";
        this.task = task;
    }

    /** {@inheritDoc} */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws BeemoException {
        tasks.add(task);
        storage.saveTasks(tasks.asList());
        ui.showAddedTask(task, tasks.size());
    }
}
