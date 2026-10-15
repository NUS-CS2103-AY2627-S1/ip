package nutrisoy.command;

import nutrisoy.exception.DukeException;
import nutrisoy.task.Task;
import nutrisoy.task.TaskList;
import nutrisoy.task.Todo;
import nutrisoy.ui.Ui;

/**
 * Adds a todo task from a user-provided description.
 */
public class TodoCommand extends Command {
    private final String description;

    /**
     * Creates a command with the todo description.
     *
     * @param description description of the todo task
     */
    public TodoCommand(String description) {
        assert description != null : "Todo description must not be null";
        this.description = description;
    }

    /** {@inheritDoc} */
    @Override
    public void execute(TaskList tasks, Ui ui) throws DukeException {
        assert tasks != null && ui != null : "Command collaborators must not be null";
        String validatedDescription = validateDescription(description, "todo");
        Task newTodo = new Todo(validatedDescription);
        addUniqueTask(newTodo, tasks, ui);
    }
}
