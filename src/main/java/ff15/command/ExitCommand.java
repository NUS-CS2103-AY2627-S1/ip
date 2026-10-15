package ff15.command;

import ff15.Storage;
import ff15.Ui;
import ff15.contact.ContactList;
import ff15.task.TaskList;

/** Says goodbye and ends the session. */
public class ExitCommand extends Command {
    /**
     * Prints the farewell message. Nothing needs saving here, because every
     * command that changes the list saves as it goes.
     */
    @Override
    public void execute(TaskList tasks, ContactList contacts, Ui ui, Storage storage) {
        ui.showFarewell();
    }

    /** Always returns true, which is what stops the main loop in {@link ff15.FF15}. */
    @Override
    public boolean isExit() {
        return true;
    }
}
