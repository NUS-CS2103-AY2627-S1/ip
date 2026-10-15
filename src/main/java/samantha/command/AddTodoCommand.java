package samantha.command;

import samantha.exception.TaskFileWriteException;
import samantha.exception.TaskValidationException;
import samantha.model.Todo;

/**
 * Represents the command that adds a todo task.
 */
public class AddTodoCommand extends Command {
    private final String taskName;

    /**
     * Creates a command for the given todo description.
     *
     * @param taskName todo description
     */
    public AddTodoCommand(String taskName) {
        this.taskName = taskName;
    }

    /**
     * Indicates that adding this task can be undone.
     *
     * @return {@code true}
     */
    @Override
    public boolean isUndoable() {
        return true;
    }

    /**
     * Adds and saves the todo task.
     *
     * @param context current application state and persistence handlers
     * @return confirmation for the newly added task
     * @throws TaskValidationException if the description is empty
     * @throws TaskFileWriteException if saving fails
     */
    @Override
    public String execute(CommandContext context)
            throws TaskValidationException, TaskFileWriteException {
        return addAndSaveTask(new Todo(taskName), context.getTasks(), context.getTaskStorage());
    }
}
