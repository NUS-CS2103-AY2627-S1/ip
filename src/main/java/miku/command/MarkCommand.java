package miku.command;

import miku.MikuException;
import miku.task.Task;

/** Marks one task as complete. */
public class MarkCommand extends TaskNumberCommand {
    /** Creates a command targeting a one-based task number. */
    public MarkCommand(int taskNumber) {
        super(taskNumber);
    }

    /** Marks the selected task complete, persists the change, and reports the result. */
    @Override
    public String execute(CommandContext commandContext) throws MikuException {
        Task task = getTask(commandContext.tasks());
        task.markAsDone();
        saveTasks(commandContext);
        return commandContext.responseFormatter().formatTaskMarked(task);
    }
}
