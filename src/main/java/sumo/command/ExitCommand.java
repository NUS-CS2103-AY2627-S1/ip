package sumo.command;

import java.io.IOException;

import sumo.storage.Storage;
import sumo.task.TaskList;
import sumo.ui.Ui;

/** Ends the current Sumo session. */
public class ExitCommand extends Command {
    /** Creates a command that ends the application. */
    public ExitCommand() {
    }

    /** {@inheritDoc} */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws IOException {
        ui.showGoodbye();
    }

    /** {@inheritDoc} */
    @Override
    public boolean isExit() {
        return true;
    }
}
