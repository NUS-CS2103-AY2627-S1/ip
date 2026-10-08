package rudra.command;

import java.util.ArrayList;

import rudra.exception.RudraException;
import rudra.storage.Storage;
import rudra.task.Task;
import rudra.task.ToDo;
import rudra.ui.Ui;

/**
 * Adds a todo task to the task list.
 */
public class TodoCommand extends Command {
    private final String description;

    /**
     * Creates a command that adds a todo with the given description.
     *
     * @param description Todo description.
     */
    public TodoCommand(String description) {
        this.description = description;
    }

    /**
     * Creates and saves the requested todo task, then shows the confirmation message.
     *
     * @param tasks Current task list.
     * @param ui UI helper used to show output.
     * @param storage Storage helper used to persist changes.
     * @throws RudraException If the task cannot be saved.
     */
    @Override
    public void execute(ArrayList<Task> tasks, Ui ui, Storage storage) throws RudraException {
        Task todoTask = new ToDo(this.description);
        addTask(tasks, todoTask, storage);
        ui.showTaskAdded(todoTask, tasks.size());
    }
}
