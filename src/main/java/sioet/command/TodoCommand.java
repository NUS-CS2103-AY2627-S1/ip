package sioet.command;

import sioet.SioetException;
import sioet.storage.Storage;
import sioet.task.Task;
import sioet.task.TaskList;
import sioet.task.Todo;
import sioet.ui.Ui;

/**
 * Represents the command that adds a todo task.
 */
public class TodoCommand extends Command {
    private final String description;

    /**
     * Creates a todo command.
     *
     * @param description the todo description
     */
    public TodoCommand(String description) {
        this.description = description;
    }

    /**
     * Executes the todo command.
     *
     * @param tasks   the task list
     * @param ui      the user interface
     * @param storage the task storage
     * @throws SioetException if the description is empty
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage)
            throws SioetException {
        if (description.isBlank()) {
            throw new SioetException(
                    "use: todo DESCRIPTION. Example: todo read chapter 5");
        }

        Task task = new Todo(description);
        tasks.add(task);
        storage.save(tasks);
        ui.showTaskAdded(task, tasks.size());
    }
}
