package tangent.command;

import tangent.Tangent;
import tangent.storage.Storage;
import tangent.task.TaskList;
import tangent.ui.Ui;

/** Terminates the program. */
public class ExitCommand extends Command {

    /**
     * Creates a command that shows the goodbye message to the user.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showGoodbye();
    }

    /**
     * Terminates {@link Tangent#run()} by causing the loop to stop.
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
