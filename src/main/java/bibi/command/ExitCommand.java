package bibi.command;

import bibi.Storage;
import bibi.Ui;
import bibi.task.TaskList;

/**
 * Says goodbye and ends the session.
 */
public class ExitCommand extends Command {
    /**
     * Creates a command that will end the session.
     */
    public ExitCommand() {
    }


    /**
     * Shows the parting message.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showGoodbye();
    }

    /**
     * Always reports true, because this is the command that ends the session.
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
