package fifi.command;

import java.io.IOException;

import fifi.Storage;
import fifi.TaskList;
import fifi.Ui;
import fifi.exception.FifiException;

/**
 * Commits task changes and success messages only after saving succeeds.
 */
public abstract class TaskChangingCommand extends Command {
    @Override
    public final boolean changesTasks() {
        return true;
    }

    @Override
    public final void execute(TaskList tasks, Ui ui, Storage storage) throws FifiException, IOException {
        TaskList candidate = tasks.copy();
        String[] messages = applyChange(candidate);
        storage.saveTasks(candidate);
        tasks.replaceWith(candidate);
        ui.showResponse(messages);
    }

    /**
     * Applies this command to an independent candidate list.
     *
     * @param candidate the list to change without touching the live tasks
     * @return success messages to display after saving
     * @throws FifiException if the requested change is invalid
     */
    protected abstract String[] applyChange(TaskList candidate) throws FifiException;
}
