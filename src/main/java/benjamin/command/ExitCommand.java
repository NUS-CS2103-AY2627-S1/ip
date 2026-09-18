package benjamin.command;

import benjamin.storage.Storage;
import benjamin.task.TaskList;
import benjamin.ui.Ui;

/** Says goodbye and stops the chatbot. */
public class ExitCommand extends Command {
    /** Shows the sign off line. Nothing is changed, so nothing is saved. */
    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) {
        ui.showFarewell();
    }

    /** Returns true, since this is the command that ends the conversation. */
    @Override
    public boolean isExit() {
        return true;
    }
}
