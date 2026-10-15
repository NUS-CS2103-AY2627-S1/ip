package pebby.command;

import pebby.storage.Storage;
import pebby.task.TaskList;
import pebby.ui.Ui;

/**
 * Ends Pebby's command loop when the user enters {@code bye}.
 */
public class ExitCommand extends Command {
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        // Pebby displays the farewell after the command loop ends.
    }

    @Override
    public boolean isExit() {
        return true;
    }
}
