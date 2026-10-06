package duke.command;

import duke.exception.DukeException;
import duke.storage.Storage;
import duke.task.TaskList;
import duke.ui.Ui;

/**
 * Represents a command to undo the most recent task modification.
 */
public class UndoCommand extends Command {
    /**
     * Executes the undo command by reverting the task list to its previous state,
     * saving the restored state, and notifying the user.
     *
     * @param tasks   The task list.
     * @param ui      The user interface.
     * @param storage The storage handler.
     * @throws DukeException If there are no commands to undo or saving fails.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws DukeException {
        tasks.undo();
        storage.save(tasks);
        ui.showUndoSuccess(tasks.size());
    }
}
