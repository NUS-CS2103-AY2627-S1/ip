package dulio.command;

import java.io.IOException;

import dulio.task.Task;
import dulio.task.TaskList;
import dulio.ui.Ui;

/**
 * Adds a parsed task to the task list.
 */
public class AddCommand extends Command {
    private Task task;

    /**
     * Creates an add command for the supplied task.
     *
     * @param task The task to add.
     */
    public AddCommand(Task task) {
        this.task = task;
    }

    /**
     * Adds the task and reports the updated count.
     *
     * @param tasks The task list to modify.
     * @param ui The interface used for the response.
     * @throws IOException If persistence fails.
     */
    @Override
    public void execute(TaskList tasks, Ui ui) throws IOException {
        if (tasks.containsEquivalent(task)) {
            ui.showError("OOPS!!! This task already exists.");
            return;
        }
        tasks.add(task);
        ui.showAdded(task, tasks.size());
    }
}
