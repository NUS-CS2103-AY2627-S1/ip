package maggigorengayam.command;

import maggigorengayam.MaggiGorengAyamException;
import maggigorengayam.storage.Storage;
import maggigorengayam.tasklist.TaskList;
import maggigorengayam.ui.Ui;

/** The {@code mark <n>} command: marks the n-th task (1-indexed) as done. */
public class MarkCommand extends Command {
    private final int taskNumber;

    /** Marks the task at the given 1-indexed position as done once {@link #execute} runs. */
    public MarkCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /** Marks the task at {@code taskNumber} as done, saves, and returns a message reporting it. */
    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage) throws MaggiGorengAyamException {
        int index = toValidIndex(taskNumber, tasks.size());
        tasks.get(index).markAsDone();
        if (saveTasks(storage, tasks)) {
            return ui.showMarked(tasks.get(index));
        }
        return ui.showSaveError();
    }
}
