package anders.command;

import anders.collection.TaskList;
import anders.storage.Storage;
import anders.ui.Ui;

/** Ends the Anders session. */
public class ExitCommand extends Command {

    /** Displays the goodbye message and leaves task data unchanged. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showGoodbye();
    }
    /** @return {@code true}, because this command ends the application */
    @Override
    public boolean isExit() {
        return true;
    }
}
