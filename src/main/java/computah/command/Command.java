package computah.command;

import computah.exception.ComputahException;
import computah.model.Model;
import computah.storage.Storage;
import computah.ui.Ui;

/**
 * Represents an executable user command.
 */
public abstract class Command {
    /**
     * Creates a command instance for subclasses.
     */
    protected Command() {
    }

    /**
     * Executes this command against the current application data and supporting components.
     *
     * @param model current application data.
     * @param ui user interface used to display command results.
     * @param storage storage used to persist application data.
     * @throws ComputahException if the command cannot be completed.
     */
    public abstract void execute(Model model, Ui ui, Storage storage) throws ComputahException;

    /**
     * Returns whether this command should end the command loop.
     *
     * @return true if the application should exit, false otherwise.
     */
    public boolean isExit() {
        return false;
    }
}
