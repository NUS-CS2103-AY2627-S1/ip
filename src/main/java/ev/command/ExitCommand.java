package ev.command;

import ev.storage.Storage;
import ev.task.TaskList;
import ev.ui.Ui;

/**
 * Ends the session.
 *
 * <p>There is nothing to do when it runs: the farewell is printed once the loop has
 * stopped, so that quitting and reaching the end of the input look the same.
 */
public class ExitCommand extends Command {

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
    }

    @Override
    public boolean isExit() {
        return true;
    }
}
