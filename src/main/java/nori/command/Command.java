package nori.command;

import nori.NoriException;
import nori.storage.Storage;
import nori.task.TaskList;
import nori.ui.Ui;

/**
 * Represents one executable user command.
 */
public abstract class Command {
    /** Creates a command for a subclass to give behaviour to. */
    protected Command() {
    }

    /**
     * Performs this command's work.
     *
     * @param tasks the stored tasks.
     * @param ui the console user interface.
     * @param storage the persistent task storage.
     * @throws NoriException if the command cannot be completed.
     */
    public abstract void execute(TaskList tasks, Ui ui, Storage storage) throws NoriException;

    /**
     * Returns whether this command ends the application.
     *
     * @return {@code true} for an exit command.
     */
    public boolean isExit() {
        return false;
    }
}
