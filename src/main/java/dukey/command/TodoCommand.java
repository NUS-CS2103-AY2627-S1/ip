package dukey.command;

import dukey.exception.DukeyException;
import dukey.storage.Storage;
import dukey.task.Task;
import dukey.task.TaskList;
import dukey.task.Todo;
import dukey.ui.Ui;

/**
 * Adds a todo task to the task list.
 */
public class TodoCommand extends Command {
    private final String description;

    /**
     * Creates a command that adds a todo task.
     *
     * @param description description of the todo task.
     */
    public TodoCommand(String description) {
        this.description = description;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws DukeyException {
        Task task = new Todo(this.description);
        tasks.add(task);
        storage.save(tasks);
        ui.showTaskAdded(task, tasks);
    }

    @Override
    public String getResponseStyleClass() {
        return STYLE_ADD;
    }
}
