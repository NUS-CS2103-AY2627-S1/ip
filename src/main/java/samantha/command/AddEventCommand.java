package samantha.command;

import samantha.exception.InputException;
import samantha.exception.TaskFileWriteException;
import samantha.exception.TaskValidationException;
import samantha.model.Event;

/**
 * Represents the command that adds an event task.
 */
public class AddEventCommand extends Command {
    private final String taskName;
    private final String from;
    private final String to;

    /**
     * Creates a command for the given event details.
     *
     * @param taskName event description
     * @param from event start time
     * @param to event end time
     */
    public AddEventCommand(String taskName, String from, String to) {
        this.taskName = taskName;
        this.from = from;
        this.to = to;
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
     * Adds and saves the event task.
     *
     * @param context current application state and persistence handlers
     * @return confirmation for the newly added task
     * @throws TaskValidationException if the event details are invalid
     * @throws InputException if an event date value is invalid
     * @throws TaskFileWriteException if saving fails
     */
    @Override
    public String execute(CommandContext context)
            throws TaskValidationException, InputException, TaskFileWriteException {
        return addAndSaveTask(new Event(taskName, from, to), context.getTasks(),
                context.getTaskStorage());
    }
}
