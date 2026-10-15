package trybot.command;

import trybot.exception.TryBotException;
import trybot.task.Task;
import trybot.task.Todo;

/**
 * Adds a todo task.
 */
public class AddTodoCommand extends AddCommand {
    private final String description;

    /**
     * Creates a todo command.
     *
     * @param description todo description.
     */
    public AddTodoCommand(String description) {
        this.description = description;
    }

    /**
     * Builds a todo from the parsed description.
     *
     * @return the todo task
     * @throws TryBotException if the description is empty
     */
    @Override
    protected Task createTask() throws TryBotException {
        if (description == null || description.isBlank()) {
            throw new TryBotException("A todo needs a description. Try: todo read book.");
        }
        return new Todo(description);
    }
}
