package samantha.command;

import samantha.exception.InputException;
import samantha.exception.TaskFileWriteException;
import samantha.exception.TaskValidationException;
import samantha.model.Deadline;

/**
 * Represents the command that adds a deadline task.
 */
public class AddDeadlineCommand extends Command {
    private final String taskName;
    private final String deadline;

    /**
     * Creates a command for the given deadline details.
     *
     * @param taskName deadline description
     * @param deadline deadline value
     */
    public AddDeadlineCommand(String taskName, String deadline) {
        this.taskName = taskName;
        this.deadline = deadline;
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
     * Adds and saves the deadline task.
     *
     * @param context current application state and persistence handlers
     * @return confirmation for the newly added task
     * @throws TaskValidationException if the description is invalid
     * @throws InputException if the deadline value is invalid
     * @throws TaskFileWriteException if saving fails
     */
    @Override
    public String execute(CommandContext context)
            throws TaskValidationException, InputException, TaskFileWriteException {
        return addAndSaveTask(new Deadline(taskName, deadline), context.getTasks(),
                context.getTaskStorage());
    }
}
