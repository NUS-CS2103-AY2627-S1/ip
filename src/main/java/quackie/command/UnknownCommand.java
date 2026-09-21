package quackie.command;

import quackie.storage.Storage;
import quackie.task.TaskList;
import quackie.ui.Ui;

/**
 * Reports a command that Quackie does not recognize.
 */
public class UnknownCommand extends Command {
    /** Creates a command for unsupported input. */
    public UnknownCommand() {
    }

    /** Reports that the entered command is not understood. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showError("I don't recognize that command.");
    }
}
