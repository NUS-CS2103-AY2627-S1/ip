package fifi.command;

import fifi.task.Task;

/**
 * Adds a todo task to the task list.
 */
public class AddTodoCommand extends AddTaskCommand {
    /**
     * Creates a command that adds the given todo.
     *
     * @param task the todo task to add
     */
    public AddTodoCommand(Task task) {
        super(task);
    }
}
