package computa.command;

import java.util.ArrayList;

import computa.storage.Storage;
import computa.task.Task;
import computa.ui.Ui;

/** Command that ends the Computa session. */
public class ExitCommand extends Command {
    /** Displays the farewell; the caller handles the loop termination. */
    @Override
    public void execute(ArrayList<Task> tasks, Ui ui, Storage storage) {
        ui.showFarewell();
    }

    /** Indicates that the command loop should stop after this command. */
    @Override
    public boolean isExit() {
        return true;
    }
}
