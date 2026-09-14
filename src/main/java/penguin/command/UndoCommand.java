package penguin.command;

import penguin.exception.PenguinException;
import penguin.storage.Storage;
import penguin.task.TaskList;
import penguin.task.TaskListSnapshot;
import penguin.ui.Ui;

/**
 * Represents a command that reverses the most recent task-list change.
 */
public class UndoCommand extends Command {
    /**
     * Creates an undo command.
     */
    public UndoCommand() {
    }

    /**
     * Restores the previous task-list state, persists it, and reports the result.
     *
     * @param tasks task list to restore.
     * @param ui interface used for output.
     * @param storage storage used to persist the restored state.
     * @throws PenguinException if there is no history or persistence fails.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws PenguinException {
        assert tasks != null : "Undo command requires a task list";
        assert ui != null : "Undo command requires a user interface";
        assert storage != null : "Undo command requires storage";

        try {
            TaskListSnapshot previousState = tasks.createSnapshot();
            tasks.undo();
            persistOrRestore(tasks, storage, previousState);
            ui.showMessage("One careful waddle backward! "
                    + "The previous action has been undone.");
        } catch (IllegalStateException e) {
            throw new PenguinException(e.getMessage());
        }
    }
}
