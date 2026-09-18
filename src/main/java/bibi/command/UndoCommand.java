package bibi.command;

import bibi.BibiException;
import bibi.Storage;
import bibi.Ui;
import bibi.task.TaskList;

/**
 * Restores the list before its latest change in the current session.
 *
 * <p>The restored list is shown with its current numbers and saved immediately,
 * just like the other commands that change tasks.
 */
public class UndoCommand extends Command {
    /**
     * Creates a command that will undo the latest list change.
     */
    public UndoCommand() {
    }

    /**
     * Restores the previous list, reports the result, and saves the change.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws BibiException {
        tasks.undo();
        ui.showMessage("Fickle, aren't ya? Last change undone. Your list holds "
                + Ui.describeCount(tasks.size()) + ".");
        showAllTasks(tasks, ui);
        saveTasks(tasks, ui, storage);
    }
}
