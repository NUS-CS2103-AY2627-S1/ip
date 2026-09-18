package eden.command;

import eden.ui.Ui;

/**
 * Ends the current Eden session.
 */
public class ExitCommand extends Command {
    /**
     * Creates a command that ends the current Eden session.
     */
    public ExitCommand() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String execute(Ui ui) {
        return ui.formatGoodbye();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
