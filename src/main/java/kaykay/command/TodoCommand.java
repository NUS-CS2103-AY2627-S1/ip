package kaykay.command;

import kaykay.model.Task;
import kaykay.model.Todo;

/**
 * Adds a todo task.
 */
public final class TodoCommand extends AddCommand {
    /**
     * Creates a todo command with its description.
     *
     * @param description description of the todo task.
     */
    public TodoCommand(String description) {
        super(description);
    }

    /**
     * Creates the todo represented by this command.
     *
     * @return a todo with the command's description.
     */
    @Override
    protected Task createTask() {
        return new Todo(getDescription());
    }
}
