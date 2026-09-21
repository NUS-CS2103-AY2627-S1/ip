package caitlyn;

import java.util.List;

/**
 * A command that ends the application.
 */
public final class ExitCommand extends Command {
    /**
     * Creates an exit command.
     */
    public ExitCommand() {
    }

    @Override
    public boolean isReadOnly() {
        return true;
    }

    /**
     * Displays the farewell message when the user exits.
     */
    @Override
    public void execute(List<Task> tasks, Ui ui) {
        ui.showFarewell();
    }

    /**
     * Returns that this command ends the application.
     *
     * @return always {@code true} for an exit command.
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
