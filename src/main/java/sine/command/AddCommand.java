package sine.command;

import java.io.IOException;

import sine.storage.Storage;
import sine.task.Task;
import sine.task.TaskList;
import sine.ui.Ui;

/**
 * Adds one parsed task to the task list.
 */
public class AddCommand extends Command {
    private final Task task;

    /**
     * Creates a command that adds the given task.
     *
     * @param task Task parsed from the user's command.
     */
    public AddCommand(Task task) {
        this.task = task;
    }

    /**
     * Adds the parsed task, saves the updated list, and shows the confirmation.
     *
     * @param tasks current task list
     * @param ui console user interface
     * @param storage task persistence service
     * @throws IOException if the updated task list cannot be saved
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws IOException {
        tasks.add(task);
        storage.save(tasks.getTasks());
        ui.showAddedTask(task, tasks.size());
    }
}
