package pdd.command;

import pdd.task.Todo;

/** Adds an already-parsed {@link Todo} to the task list. */
public class TodoCommand extends AddTaskCommand {
    /** Creates a command that will add the given already-parsed todo to the task list. */
    public TodoCommand(Todo todo) {
        super(todo);
    }
}
