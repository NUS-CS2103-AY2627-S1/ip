package sine.command;

import java.io.IOException;

import sine.exception.SineException;
import sine.storage.Storage;
import sine.task.TaskList;
import sine.ui.Ui;

/** Reverses one task change from the current session and saves the restored list. */
public class UndoCommand extends Command {
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws IOException, SineException {
        String details = tasks.undo();
        // Report the in-memory change even when saving subsequently fails.
        ui.showUndo(details);
        storage.save(tasks.getTasks());
    }
}
