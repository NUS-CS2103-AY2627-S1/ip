package oreo.command;

import oreo.core.AppContext;
import oreo.core.OreoException;
import oreo.core.Parser;
import oreo.model.Task;

/** Shared workflow for commands that change one task's completion status. */
public abstract class TaskStatusCommand extends Command {
    private final String taskNumber;

    /** Creates a status command for the supplied one-based task number. */
    protected TaskStatusCommand(String taskNumber, Parser parser) {
        super(parser);
        this.taskNumber = taskNumber;
    }

    /** Applies the requested status to the task. */
    protected abstract void update(Task task);

    /** Returns the confirmation text for the status change. */
    protected abstract String message();

    /** Updates, persists, and displays the selected task's new status.
     *
     * @param context the application state and collaborators used by the command
     * @throws OreoException if the task number is invalid
     */
    @Override
    public void execute(AppContext context) throws OreoException {
        Task task = context.getTasks().get(parser.taskIndex(taskNumber, context.getTasks().size()));
        update(task);
        context.getStorage().save(context.getTasks());
        context.getUi().showSuccess(message(), task);
    }
}
