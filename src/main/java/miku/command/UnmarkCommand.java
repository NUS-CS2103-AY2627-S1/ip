package miku.command;

import miku.MikuException;
import miku.task.Task;

/** Marks one task as incomplete. */
public class UnmarkCommand extends TaskNumberCommand {
    /** Creates a command targeting a one-based task number. */
    public UnmarkCommand(int taskNumber) {
        super(taskNumber);
    }

    /** Marks the selected task incomplete, persists the change, and reports the result. */
    @Override
    public String execute(CommandContext commandContext) throws MikuException {
        Task task = getTask(commandContext.tasks());
        task.markAsNotDone();
        saveTasks(commandContext);
        return commandContext.responseFormatter().formatTaskUnmarked(task);
    }
}
