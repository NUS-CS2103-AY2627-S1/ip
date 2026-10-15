package thomas.command;

import thomas.ThomasException;
import thomas.storage.Storage;
import thomas.task.TaskList;
import thomas.ui.Ui;

/**
 * Takes back the most recent change to the task list: the {@code undo} command.
 * <p>
 * One change at a time, most recent first, back to the start of the session.
 * What it would take to reverse each change is {@link History}'s to remember, so
 * all this class does is ask for the next one to be applied and say which line
 * it was that has been taken back.
 * <p>
 * Undoing is not itself a change to take back: this command records nothing, so
 * a second {@code undo} reaches the change before the first one rather than
 * putting it back. There is no redo.
 */
public class UndoCommand extends Command {
    /**
     * Reverses the most recent change, saves, and says what was undone.
     * <p>
     * The saving matters as much as the undoing: every command that changed the
     * list wrote the file as it went, so a list put back only in memory would be
     * the old one again on the next run.
     *
     * @param tasks The list to put back as it was.
     * @param ui Used to word the confirmation.
     * @param storage Where the restored list is written.
     * @param history Where the change being reversed was recorded.
     * @return The confirmation, behind a warning if the save failed.
     * @throws ThomasException If nothing has changed the list that has not
     *                         already been undone.
     */
    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage, History history)
            throws ThomasException {
        String typedLine = history.undo(tasks);
        String saveWarning = save(tasks, ui, storage);
        return saveWarning + ui.getUndoneMessage(typedLine, tasks.size());
    }
}
