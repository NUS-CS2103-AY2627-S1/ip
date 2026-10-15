package et.command;

import et.storage.Storage;
import et.task.Task;
import et.task.TaskList;
import et.ui.Ui;

/**
 * Adds one parsed task to ET's task list.
 */
public class AddCommand extends Command {
    /** The task to add to ET's task list. */
    private final Task task;

    /**
     * Creates a command that adds the supplied task.
     *
     * @param task the task to add
     */
    public AddCommand(Task task) {
        this.task = task;
    }

    /** {@inheritDoc} */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        tasks.addTask(task);
        saveTasks(tasks, ui, storage);
        ui.showTaskAdded(task, tasks.size());
    }
}
