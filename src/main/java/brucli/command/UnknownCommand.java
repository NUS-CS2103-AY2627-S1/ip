package brucli.command;

import brucli.storage.Storage;
import brucli.task.TaskList;
import brucli.ui.ResponseType;
import brucli.ui.Ui;

/**
 * Handles input that does not match a supported BruCLI command.
 */
public class UnknownCommand extends Command {

    /**
     * Displays a command-not-recognized response.
     */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showUnknownCommand();
    }

    @Override
    public ResponseType getResponseType() {
        return ResponseType.ERROR;
    }
}
