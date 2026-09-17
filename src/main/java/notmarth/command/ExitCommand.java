package notmarth.command;

import notmarth.model.ContactList;
import notmarth.model.TaskList;
import notmarth.storage.Storage;
import notmarth.ui.Ui;

/** Executes the command that ends the NotMarth session. */
public final class ExitCommand extends Command {
    /** Creates an exit command. */
    public ExitCommand() {
    }

    /**
     * Displays the farewell message for the ending session.
     *
     * @param tasks the current task list, which is unchanged.
     * @param ui the console interaction handler.
     * @param storage the task archive handler, which is unused.
     */
    @Override
    public void execute(TaskList tasks, ContactList contacts, Ui ui, Storage storage) {
        ui.showFarewell();
    }

    /**
     * Reports that this command ends the command loop.
     *
     * @return always {@code true}.
     */
    @Override
    public boolean isExit() {
        return true;
    }
}
