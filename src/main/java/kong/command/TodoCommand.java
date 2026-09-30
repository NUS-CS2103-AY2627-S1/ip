package kong.command;

import kong.exception.KongException;
import kong.storage.Storage;
import kong.task.Task;
import kong.task.TaskList;
import kong.task.Todo;
import kong.ui.Ui;

/**
 * Adds a todo task to the task list.
 */
public class TodoCommand extends Command {
    private final String description;

    /**
     * Creates a command that adds a todo.
     *
     * @param description task description
     */
    public TodoCommand(String description) {
        this.description = description;
    }

    /** {@inheritDoc} */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws KongException {
        Task task = new Todo(description);
        if (tasks.hasDuplicate(task)) {
            throw new KongException("This task already exists in your list.");
        }
        int previousTaskCount = tasks.size();
        tasks.add(task);
        assert tasks.size() == previousTaskCount + 1 : "Adding a task must increase the task count by one";
        ui.showTaskAdded(task);
        saveTasks(tasks, storage);
    }
}
