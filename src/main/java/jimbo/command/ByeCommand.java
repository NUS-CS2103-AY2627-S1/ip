package jimbo.command;

import jimbo.Storage;
import jimbo.TaskList;
import jimbo.ui.Ui;

/**
 * Command for exiting the app.
 */
public class ByeCommand extends Command {
    @Override
    public String execute(Ui ui, TaskList tasks, Storage storage) {
        return "bye bye!";
    }

    @Override
    public boolean shouldQuit() {
        return true;
    }
}
