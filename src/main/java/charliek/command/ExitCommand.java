package charliek.command;

import charliek.ui.Ui;

/**
 * Handles the command that ends the current application session.
 */
public class ExitCommand extends Command {
    /**
     * The UI used to show the goodbye message.
     */
    private final Ui ui;

    /**
     * Creates an exit command.
     *
     * @param ui the UI used to show the goodbye message.
     */
    public ExitCommand(Ui ui) {
        this.ui = ui;
    }

    /**
     * Shows the goodbye message for the current application session.
     */
    @Override
    public void execute() {
        ui.showGoodbye();
    }

    /**
     * Indicates that executing this command should end the application session.
     *
     * @return {@code true} because this is the exit command.
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
