package glennon.command;

import glennon.Storage;
import glennon.Ui;
import glennon.task.TaskList;

/**
 * Ends the current Glennon session.
 */
public final class ExitCommand extends Command {
    /**
     * Creates a command that ends the current session.
     */
    public ExitCommand() {
    }

    /**
     * Displays Glennon's goodbye message.
     *
     * @param missions missions in the current session.
     * @param ui interface used to display the result.
     * @param storage storage used by the application.
     */
    @Override
    public void execute(TaskList missions, Ui ui, Storage storage) {
        ui.showGoodbye();
    }

    /**
     * Reports that this command ends the application.
     *
     * @return true.
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
