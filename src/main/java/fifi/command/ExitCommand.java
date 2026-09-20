package fifi.command;

import fifi.Storage;
import fifi.TaskList;
import fifi.Ui;

/**
 * Ends the chatbot session.
 */
public class ExitCommand extends Command {
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showResponse("BaiBai! Hope to see you soon ^^");
    }

    @Override
    public boolean isExit() {
        return true;
    }
}
