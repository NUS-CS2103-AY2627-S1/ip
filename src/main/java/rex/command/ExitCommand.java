package rex.command;

import rex.TaskList;
import rex.Ui;

/** Says goodbye and ends the session. */
public class ExitCommand extends Command {
    /** Says goodbye. Ending the session is left to isExit(). */
    @Override
    public void execute(TaskList tasks, Ui ui) {
        ui.showFarewell();
    }

    /** Returns true: this is the one command that ends the session. */
    @Override
    public boolean isExit() {
        return true;
    }
}
