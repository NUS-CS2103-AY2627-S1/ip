package computah.command;

import java.util.ArrayList;

import computah.exception.ComputahException;
import computah.model.Model;
import computah.storage.Storage;
import computah.task.Task;
import computah.ui.Ui;

/**
 * Adds a task to the task list.
 */
public class AddCommand extends Command {
    private final Task task;

    /**
     * Creates a command that adds the given task.
     *
     * @param task task to add.
     */
    public AddCommand(Task task) {
        this.task = task;
    }

    /**
     * Adds the task, persists the updated list, and shows the confirmation message.
     *
     * @param model current application data.
     * @param ui user interface used to display the confirmation.
     * @param storage storage used to persist the updated task list.
     * @throws ComputahException if the updated task list cannot be saved.
     */
    @Override
    public void execute(Model model, Ui ui, Storage storage) throws ComputahException {
        ArrayList<Task> tasks = model.getTasks();
        int previousTaskCount = tasks.size();
        tasks.add(task);
        assert tasks.size() == previousTaskCount + 1 : "Adding a task should increase the task count by one";
        storage.saveTasks(tasks);
        ui.showTaskAdded(task, tasks.size());
    }
}
