package thomas.command;

import thomas.ThomasException;
import thomas.storage.Storage;
import thomas.task.Task;
import thomas.task.TaskList;
import thomas.task.Todo;
import thomas.ui.Ui;

/**
 * Adds a todo task to the task list.
 */
public class TodoCommand extends Command {
    private final String description;

    /**
     * Creates a command that adds a todo task.
     *
     * @param description Description of the todo.
     */
    public TodoCommand(String description) {
        this.description = description;
    }

    /**
     * Adds the todo, saves the list, and confirms the addition.
     *
     * @param tasks task list to update
     * @param ui user interface used for output
     * @param storage storage used to persist the change
     * @throws ThomasException if the description is empty
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws ThomasException {
        if (description.isEmpty()) {
            throw new ThomasException("Todo description cannot be empty.");
        }
        Task task = new Todo(description);
        if (tasks.containsEquivalent(task)) {
            throw new ThomasException("This task already exists.");
        }
        tasks.add(task);
        storage.save(tasks);
        ui.showTaskAdded(task, tasks.size());
    }
}
