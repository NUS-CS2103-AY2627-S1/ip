package meowmeow.command;

import meowmeow.MeowmeowException;
import meowmeow.storage.Storage;
import meowmeow.task.Task;
import meowmeow.task.TaskList;
import meowmeow.ui.Ui;

/**
 * Removes the task at a given 1-based position (the number shown by
 * "list") - the "delete" command.
 */
public class DeleteCommand extends Command {
    private final int taskNumber;

    /**
     * Constructs a command that deletes the task at the given position.
     *
     * @param taskNumber the 1-based position (as shown by "list") to remove.
     */
    public DeleteCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage) throws MeowmeowException {
        // tasks.delete rejects an out-of-range position with the "doesn't
        // exist" message.
        Task removed = tasks.delete(taskNumber);
        storage.save(tasks.asList());
        return ui.showRemoved(removed, tasks.size());
    }
}
