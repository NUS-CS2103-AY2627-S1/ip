package crystal.command;

import java.util.ArrayList;
import java.util.List;

import crystal.exception.CrystalException;
import crystal.storage.Storage;
import crystal.task.Task;
import crystal.task.TaskList;
import crystal.ui.Ui;

/**
 * Adds one parsed task to the task list and persists the result.
 */
public final class AddCommand extends Command {
    /** Task to add when the command executes. */
    private final Task task;

    /**
     * Creates an add command for a parsed task.
     *
     * @param task task to add.
     */
    public AddCommand(Task task) {
        this.task = task;
    }

    /**
     * Adds, saves, and displays the task.
     *
     * @param tasks task list to mutate.
     * @param ui console interface used to present the result.
     * @param storage persistence service for the updated task list.
     * @throws CrystalException if the updated task list cannot be saved.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws CrystalException {
        List<Task> updatedTasks = new ArrayList<>(tasks.getTasks());
        updatedTasks.add(task);
        storage.saveTasks(updatedTasks);
        tasks.addTask(task);
        ui.showTaskAdded(task, tasks.getTaskCount());
    }
}
