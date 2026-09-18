package benjamin.command;

import benjamin.BenjaminException;
import benjamin.storage.Storage;
import benjamin.task.Task;
import benjamin.task.TaskList;
import benjamin.ui.Ui;

/** Removes a label from one task. */
public class UntagCommand extends Command {
    private final int taskNumber;
    private final String tag;

    /**
     * Creates a command that will remove a label from the given task.
     *
     * @param taskNumber the one based position the user typed, checked when
     *     the command runs.
     * @param tag the label to remove, already stripped of any leading hash.
     */
    public UntagCommand(int taskNumber, String tag) {
        this.taskNumber = taskNumber;
        this.tag = tag;
    }

    /**
     * Removes the label and saves, or says so if the task did not have it.
     *
     * @throws BenjaminException if there is no such task, or it cannot be saved.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws BenjaminException {
        int index = toIndex(taskNumber, "untag", tasks);
        Task task = tasks.get(index);

        if (!task.removeTag(tag)) {
            ui.showNotTagged(tag);
            return;
        }

        ui.showUntagged(task);
        storage.save(tasks);
    }
}
