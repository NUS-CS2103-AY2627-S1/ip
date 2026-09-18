package herta.command;

import herta.task.Todo;

/**
 * Represents the command that adds a todo task.
 */
public class TodoCommand extends AddCommand {

    /**
     * Creates a command that adds the given todo task.
     *
     * @param todo the todo task to add.
     */
    public TodoCommand(Todo todo) {
        super(todo);
    }
}
