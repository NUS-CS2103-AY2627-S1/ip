package alfred.command;

import alfred.Storage;
import alfred.Ui;
import alfred.task.TaskList;

/**
 * Says goodbye and ends the conversation. Made for a {@code bye}, and when the
 * input runs out.
 */
public class ExitCommand extends Command {
    /** Creates a command that will say goodbye and end the session. */
    public ExitCommand() {
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showFarewell();
    }

    @Override
    public boolean isExit() {
        return true;
    }
}
