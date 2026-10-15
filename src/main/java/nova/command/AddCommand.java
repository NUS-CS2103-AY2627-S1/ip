package nova.command;

import java.io.IOException;

import nova.task.Task;
import nova.task.TaskList;
import nova.ui.Ui;

/**
 * Adds a task to the task list.
 */
public class AddCommand extends Command {
    private final Task task;

    /**
     * Creates a command that adds the given task.
     *
     * @param task task to add
     */
    public AddCommand(Task task) {
        this.task = task;
    }

    /**
     * Adds the task, saves the updated task list, and displays the result.
     *
     * @param tasks task list to update
     * @param ui UI used to display the added task
     * @throws IOException if the updated task list cannot be saved
     */
    @Override
    public void execute(TaskList tasks, Ui ui) throws IOException {
        tasks.add(task);
        ui.showAddedTask(task, tasks.size());
    }
}
