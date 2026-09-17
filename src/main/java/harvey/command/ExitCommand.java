package harvey.command;

import harvey.storage.Storage;
import harvey.task.TaskList;
import harvey.ui.Ui;
/**
 * Ends the session.
 * <p>
 * Nothing is changed or saved; the command's only effects are the parting message it
 * returns and the {@code true} from {@link #isExit()}, which tells the caller to stop.
 */
public class ExitCommand extends Command {
    @Override
    public String execute(TaskList tasks, Ui ui, Storage storage) {
        return ui.formatFarewell();
    }

    @Override
    public boolean isExit() {
        return true;
    }
}
