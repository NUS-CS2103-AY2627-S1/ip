package victoria.command;

import victoria.model.TaskList;
import victoria.ui.Ui;

/** A command that ends the interactive session. */
public class ExitCommand extends Command {
    /** Displays the farewell message when the user ends the session. */
    @Override
    public void execute(TaskList tasks, Ui ui) {
        ui.showFarewell();
    }

    /** Returns {@code true} because this command ends the session. */
    @Override
    public boolean isExit() {
        return true;
    }
}
