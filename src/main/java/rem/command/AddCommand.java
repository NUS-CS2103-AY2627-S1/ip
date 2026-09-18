package rem.command;

import java.io.IOException;

import rem.exception.RemException;
import rem.storage.Storage;
import rem.task.Task;
import rem.task.TaskList;
import rem.ui.Ui;

/**
 * Adds a task to the task list.
 */
public class AddCommand extends Command {
    private final Task task;

    /**
     * Creates a command for an already-parsed task.
     *
     * @param task Task to add.
     */
    public AddCommand(Task task) {
        this.task = task;
    }

    /**
     * Adds the task, saves the task list, and displays the result.
     *
     * @param tasks Tasks managed by Rem.
     * @param ui User interface used to display the result.
     * @param storage Storage used to save the updated task list.
     * @throws RemException If an identical task already exists.
     * @throws IOException If the updated task list cannot be saved.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws IOException, RemException {
        tasks.validateUnique(task);
        tasks.add(task);
        storage.saveTasks(tasks.getTasks());
        ui.showMessages("Got it! I put it on the list:", task.toString());
        if (tasks.size() == 1) {
            ui.showMessage("Yay! Our first task!");
        } else {
            ui.showMessage("Now you have " + tasks.size() + " tasks in the list.");
        }
    }
}
