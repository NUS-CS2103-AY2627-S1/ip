package benjamin.command;

import benjamin.BenjaminException;
import benjamin.storage.Storage;
import benjamin.task.TaskList;
import benjamin.ui.Ui;

/** Marks one task as not done yet. */
public class UnmarkCommand extends Command {
    private final int taskNumber;

    /**
     * Creates a command that will mark the given task as not done.
     *
     * @param taskNumber the one based position the user typed, checked when
     *     the command runs.
     */
    public UnmarkCommand(int taskNumber) {
        this.taskNumber = taskNumber;
    }

    /**
     * Marks the chosen task as not done, reports it, and saves the change.
     *
     * @throws BenjaminException if there is no such task, or it cannot be saved.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws BenjaminException {
        int index = toIndex(taskNumber, "unmark", tasks);

        tasks.get(index).markAsNotDone();
        ui.showUnmarked(tasks.get(index));
        storage.save(tasks);
    }
}
