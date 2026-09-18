package toothless.command;

import toothless.task.Task;
import toothless.task.Todo;

/**
 * Coordinates creating a todo and persisting the updated task list.
 */
public class TodoCommand extends AddCommand {
    private final String description;

    /**
     * Creates a todo command for a validated description.
     *
     * @param description todo description obtained from the parser.
     */
    public TodoCommand(String description) {
        this.description = description;
    }

    /**
     * Creates the todo handled by the shared add workflow.
     *
     * @return todo created from the parsed description
     */
    @Override
    protected Task createTask() {
        return new Todo(description);
    }
}
