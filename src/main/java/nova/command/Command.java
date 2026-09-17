package nova.command;

import nova.exception.NovaException;
import nova.storage.Storage;
import nova.task.TaskList;
import nova.ui.Ui;

/**
 * Represents an executable command understood by Nova.
 */
public abstract class Command {
    /**
     * Performs this command using Nova's application components.
     *
     * @param tasks task list to query or update.
     * @param ui user interface used to display command results.
     * @param storage storage used to persist task changes.
     * @throws NovaException if the command cannot be completed.
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws NovaException;

    /**
     * Returns whether Nova should stop after executing this command.
     *
     * @return {@code true} only for an exit command.
     */
    public boolean isExit() {
        return false;
    }
}
