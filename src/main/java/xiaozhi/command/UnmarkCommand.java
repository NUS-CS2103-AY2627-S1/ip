package xiaozhi.command;

import xiaozhi.exception.XiaoZhiException;
import xiaozhi.storage.Storage;
import xiaozhi.task.Task;
import xiaozhi.task.TaskList;
import xiaozhi.ui.Ui;

/**
 * Marks a task in the task list as not done.
 */
public class UnmarkCommand extends Command {
    private final int targetIndex;
    private boolean wasDoneBefore;

    /**
     * Creates an UnmarkCommand for the task at the given position.
     *
     * @param targetIndex 0-based index, within the task list, of the task to unmark.
     */
    public UnmarkCommand(int targetIndex) {
        this.targetIndex = targetIndex;
    }

    /**
     * Marks the target task as not done, reports it through {@code ui}, and
     * saves the updated list through {@code storage}.
     *
     * @throws XiaoZhiException If {@code targetIndex} is not a valid position in {@code tasks}.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage, CommandHistory history) throws XiaoZhiException {
        if (targetIndex < 0 || targetIndex >= tasks.size()) {
            throw new XiaoZhiException(
                    "There is no task " + (targetIndex + 1) + " to speak of. Only " + tasks.size()
                    + " task(s) exist; look again before you act.");
        }
        Task target = tasks.get(targetIndex);
        wasDoneBefore = target.isDone();
        target.markAsNotDone();
        ui.showUnmarked(target);
        storage.save(tasks.asList());
    }

    /**
     * Returns {@code true} -- unmarking a task can always be undone by
     * restoring whatever its done status was beforehand.
     */
    @Override
    public boolean isUndoable() {
        return true;
    }

    /**
     * Restores the target task's done status to what it was right before this
     * command unmarked it, reports the reversal through {@code ui}, and saves
     * the updated list through {@code storage}.
     * <p>
     * Restoring rather than unconditionally marking matters when the task was
     * already not done before this command ran (e.g. {@code unmark 1} used
     * twice in a row): undoing the second, no-op unmark should leave it not done.
     */
    @Override
    public void undo(TaskList tasks, Ui ui, Storage storage) {
        Task target = tasks.get(targetIndex);
        if (wasDoneBefore) {
            target.markAsDone();
            ui.showMarked(target);
        } else {
            target.markAsNotDone();
            ui.showUnmarked(target);
        }
        storage.save(tasks.asList());
    }
}
