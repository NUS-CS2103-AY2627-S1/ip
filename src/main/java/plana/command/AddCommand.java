package plana.command;

import java.util.List;

import plana.exception.PlanaException;
import plana.storage.Storage;
import plana.task.Task;
import plana.task.TaskList;
import plana.ui.Ui;

/**
 * Adds a task to the list and persists the updated task list.
 */
public class AddCommand extends Command {
    private final Task task;

    /**
     * Creates an add command for a fully constructed task.
     *
     * @param task the task to add.
     */
    public AddCommand(Task task) {
        this.task = task;
    }

    /**
     * Adds the task, saves the list, and displays the result.
     *
     * @param tasks the task list to update.
     * @param ui the user interface used for the response.
     * @param storage the storage collaborator used for persistence.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws PlanaException {
        if (tasks.containsSameTask(task)) {
            throw new PlanaException("Oops, that task already exists. Change its details or edit the existing task.");
        }
        List<Task> savedTasks = tasks.copyTasks();
        tasks.add(task);
        if (!storage.saveTasks(tasks)) {
            tasks.restoreTasks(savedTasks);
            throw new PlanaException("Oops, I couldn't save that task. Please check that the data folder is writable.");
        }
        ui.showTaskAdded(task, tasks.size());
    }
}
