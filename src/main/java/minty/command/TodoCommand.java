package minty.command;

import minty.exception.MintyException;
import minty.task.Task;

/**
 * Adds a todo to Minty's task list.
 */
public class TodoCommand extends AddCommand {

    /**
     * Creates a todo command from the user's complete input.
     *
     * @param fullCommand complete todo command.
     */
    public TodoCommand(String fullCommand) {
        super(fullCommand);
    }

    /**
     * Parses the todo represented by the complete command.
     *
     * @param fullCommand complete todo command.
     * @return validated todo.
     * @throws MintyException if the todo description is missing.
     */
    @Override
    protected Task createTask(String fullCommand) throws MintyException {
        return Parser.parseTodo(fullCommand);
    }
}
