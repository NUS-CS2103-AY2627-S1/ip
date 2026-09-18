package meowmeow.command;

import meowmeow.storage.Storage;
import meowmeow.task.TaskList;
import meowmeow.ui.Ui;

/**
 * Says goodbye and ends the session - the "bye" command. The only command
 * whose {@link #isExit()} is {@code true}, which is how the command loop
 * knows to stop.
 */
public class ExitCommand extends Command {

    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage) {
        return ui.showFarewell();
    }

    @Override
    public boolean isExit() {
        return true;
    }
}
