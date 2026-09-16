package zucc.command;

import zucc.ZuccException;
import zucc.task.Priority;
import zucc.task.Task;
import zucc.task.Todo;

/**
 * Adds a to-do task described by the command's raw main argument.
 */
public final class TodoCommand extends AddTaskCommand {
    /** Creates a to-do command awaiting values from Parser. */
    TodoCommand() {
        super("todo", "/priority");
    }

    /**
     * Validates the required description and constructs a to-do task.
     *
     * @return constructed to-do task.
     * @throws ZuccException if the description is blank.
     */
    @Override
    protected Task createTask() throws ZuccException {
        return new Todo(
                requireArgument("a description"),
                Priority.fromUserInput(getOption("/priority")));
    }
}
