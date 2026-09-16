package brucli.command;

import brucli.storage.Storage;
import brucli.task.TaskList;
import brucli.ui.ResponseType;
import brucli.ui.Ui;

/**
 * Handles BruCLI's deliberately unsupported sudo command.
 */
public class SudoCommand extends Command {

    /**
     * Explains that elevated privileges are unavailable.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showSudoDenied();
    }

    @Override
    public ResponseType getResponseType() {
        return ResponseType.JOKE;
    }
}
