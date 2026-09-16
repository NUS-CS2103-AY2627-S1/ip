package miku.command;

import miku.MikuException;
import miku.task.Task;

/** Removes one task from Miku's task list. */
public class DeleteCommand extends TaskNumberCommand {
    /** Creates a command targeting a one-based task number. */
    public DeleteCommand(int taskNumber) {
        super(taskNumber);
    }

    /** Removes the selected task, persists the updated list, and reports the result. */
    @Override
    public String execute(CommandContext commandContext) throws MikuException {
        Task removedTask = commandContext.tasks().remove(getTaskIndex(commandContext.tasks()));
        saveTasks(commandContext);
        return commandContext.responseFormatter().formatTaskDeleted(removedTask, commandContext.tasks().size());
    }
}
