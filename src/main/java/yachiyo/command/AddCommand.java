package yachiyo.command;

import java.util.ArrayList;
import java.util.List;

import yachiyo.exception.YachiyoException;
import yachiyo.storage.Storage;
import yachiyo.task.Task;
import yachiyo.task.TaskList;
import yachiyo.ui.Ui;

/**
 * Adds a task to the task list and saves the updated list.
 */
public class AddCommand extends Command {
    private final Task task;

    /**
     * Creates a command that adds the supplied task.
     *
     * @param task task to add.
     */
    public AddCommand(Task task) {
        this.task = task;
    }

    /**
     * Adds the task, persists the updated list, and displays confirmation.
     *
     * @param tasks task list to update.
     * @param ui user interface used to display confirmation.
     * @param storage storage used to persist the updated list.
     * @throws YachiyoException if the updated task list cannot be saved.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws YachiyoException {
        List<Task> updatedTasks = new ArrayList<>(tasks.getTasks());
        updatedTasks.add(task);

        storage.saveTasks(updatedTasks);
        tasks.add(task);
        ui.showTaskAdded(task, tasks.size());
    }
}
