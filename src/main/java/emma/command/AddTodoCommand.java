package emma.command;

import emma.Task;
import emma.Todo;

/**
 * Adds a task with no timing attached.
 */
public class AddTodoCommand extends AddCommand {

    private final String description;

    /**
     * Creates the command.
     *
     * @param description what the task is.
     */
    public AddTodoCommand(String description) {
        this.description = description;
    }

    @Override
    protected Task createTask() {
        return new Todo(description);
    }
}
