package sallman.command;

import sallman.SallmanException;
import sallman.Storage;
import sallman.TaskList;
import sallman.Ui;

/**
 * Puts the task list back as it was before the most recent change.
 * <p>
 * Only changes made since the chatbot started can be undone, since the history
 * lives in memory rather than in the data file.
 */
public class UndoCommand extends Command {

    /**
     * Restores the previous state of the list, reports it, and saves.
     *
     * @param tasks   the list to restore
     * @param ui      used to confirm the undo
     * @param storage used to save the restored list
     * @throws SallmanException if nothing has been changed yet, or if the
     *                          restored list cannot be saved
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws SallmanException {
        tasks.undo();
        ui.showUndone(tasks.size());
        storage.save(tasks.asList());
    }
}
