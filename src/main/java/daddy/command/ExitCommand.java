package daddy.command;

import daddy.storage.Storage;
import daddy.task.TaskList;
import daddy.ui.Ui;

/**
 * Ends the Daddy chat session.
 */
public class ExitCommand extends Command {
    /** Creates a command that ends the application. */
    public ExitCommand() {
    }

    /** Displays the farewell message. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showExit();
    }

    /** This command ends the application. */
    @Override
    public boolean isExit() {
        return true;
    }
}
