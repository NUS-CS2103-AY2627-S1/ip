package serangooner.command;

import serangooner.storage.Storage;
import serangooner.task.TaskList;
import serangooner.ui.Ui;

/**
 * Reverses the most recent change made to the list.
 * The list is written back only when a change was reversed, so that asking
 * to undo with nothing to undo leaves the save file untouched.
 */
public class UndoCommand extends Command {
    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage) {
        boolean isUndone = tasks.undo();
        if (isUndone) {
            storage.save(tasks.getTasks());
        }
        return ui.formatUndo(isUndone);
    }
}
