package duncan.command;

import duncan.DuncanException;
import duncan.Storage;
import duncan.Ui;
import duncan.task.TaskList;

/** Says goodbye and signals the main loop to stop. */
public class ExitCommand extends Command {
    /** {@inheritDoc} */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws DuncanException {
        ui.showGoodbye();
    }

    /** Always returns true, since this command ends the program's main loop. */
    @Override
    public boolean isExit() {
        return true;
    }
}
