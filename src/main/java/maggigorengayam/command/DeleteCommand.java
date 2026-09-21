package maggigorengayam.command;

import maggigorengayam.MaggiGorengAyamException;
import maggigorengayam.storage.Storage;
import maggigorengayam.task.Task;
import maggigorengayam.tasklist.TaskList;
import maggigorengayam.ui.Ui;

/** The {@code delete <n>} command: removes the n-th task (1-indexed) from the list. */
public class DeleteCommand extends Command {
    private final int taskNumber;

    /** Deletes the task at the given 1-indexed position once {@link #execute} runs. */
    public DeleteCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /** Removes the task at {@code taskNumber}, saves, and returns a message reporting the removal. */
    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage) throws MaggiGorengAyamException {
        int index = toValidIndex(taskNumber, tasks.size());
        Task removed = tasks.remove(index);
        if (saveTasks(storage, tasks)) {
            return ui.showRemoved(removed, tasks.size());
        }
        return ui.showSaveError();
    }
}
