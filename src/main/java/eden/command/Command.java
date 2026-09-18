package eden.command;

import eden.exception.EdenException;
import eden.ui.Ui;

/**
 * Represents an action that Eden can execute.
 */
public abstract class Command {
    /**
     * Creates a command.
     */
    public Command() {
    }

    /**
     * Executes this command and formats its result for display.
     *
     * @param ui user interface used to display the result.
     * @return response to show to the user.
     * @throws EdenException if the command cannot be completed.
     */
    public abstract String execute(Ui ui) throws EdenException;

    /**
     * Returns whether Eden should stop after executing this command.
     *
     * @return true only for a command that exits Eden.
     */
    public boolean isExit() {
        return false;
    }
}
