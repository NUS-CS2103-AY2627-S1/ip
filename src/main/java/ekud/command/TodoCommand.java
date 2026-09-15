package ekud.command;

import ekud.EkudException;
import ekud.storage.Storage;
import ekud.task.Task;
import ekud.task.Todo;

/**
 * Adds a {@link Todo} to the task list.
 */
public class TodoCommand implements TaskCreatingCommand {
    private final String description;

    /**
     * Creates a command that will add a todo with the given description.
     *
     * @param description text of the todo to add
     */
    private TodoCommand(String description) {
        this.description = description;
    }

    /**
     * Parses {@code todo DESCRIPTION}.
     *
     * @param arguments text after the command word
     * @return a todo command
     * @throws EkudException if the description is missing
     */
    public static TodoCommand parse(String arguments) throws EkudException {
        if (arguments.isBlank()) {
            throw new EkudException("The description of a todo cannot be empty.");
        }
        return new TodoCommand(arguments.trim());
    }

    /**
     * Builds the todo without adding it to the list.
     * {@link Storage} uses this when loading a saved {@code todo} line.
     *
     * @return a new incomplete todo
     */
    @Override
    public Task createTask() {
        return new Todo(description);
    }
}
