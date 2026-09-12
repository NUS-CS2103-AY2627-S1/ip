package pixel.command;

import pixel.storage.Storage;
import pixel.task.TaskList;
import pixel.ui.Ui;

/**
 * Responds to an unrecognized user command.
 */
public class UnknownCommand extends Command {

    /** Creates a command for unrecognized input. */
    public UnknownCommand() {
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showMessage("Sorry, I don't recognise that command.");
    }

    @Override
    public boolean isError() {
        return true;
    }
}
