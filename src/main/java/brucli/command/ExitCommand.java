package brucli.command;

import brucli.storage.Storage;
import brucli.task.TaskList;
import brucli.ui.Ui;

/**
 * Ends the current BruCLI session.
 */
public class ExitCommand extends Command {

    /**
     * Displays the goodbye response.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showGoodbye();
    }

    /**
     * Indicates that the command loop should stop after execution.
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
